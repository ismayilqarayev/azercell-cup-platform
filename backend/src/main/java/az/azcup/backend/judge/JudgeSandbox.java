package az.azcup.backend.judge;

import az.azcup.backend.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Şagird kodunun (g++ kompilyasiyası və compile olunmuş proqramın icrası) təhlükəsiz işə salınması.
 *
 * <p>Əvvəllər şagird proqramı tətbiqin öz prosesinin mühit dəyişənlərini (DB_PASSWORD, JWT_SECRET, ADMIN_PASSWORD,
 * GITHUB_TOKEN) və HÜQUQLARINI miras alırdı: {@code getenv("JWT_SECRET")} və ya {@code /proc/1/environ} oxuyan proqram
 * (həm də {@code #include "/proc/1/environ"} ilə kompilyasiya xətası mesajı) bu sirləri çıxışda qaytara bilərdi.
 * Üç qat müdafiə qoyulur:</p>
 * <ol>
 *   <li><b>Mühit təmizlənir</b> ({@link #prepare}): alt prosesə yalnız PATH/TMP kimi zəruri dəyişənlər verilir
 *       (qara siyahı yox, ağ siyahı — yeni gizli dəyişən əlavə olunsa da sızmır).</li>
 *   <li><b>Hüquqlar endirilir</b> ({@link Lease#wrap}): {@code app.judge.sandbox.run-as-uid} təyin olunubsa (production,
 *       Linux, tətbiq root-dur), komanda {@code prlimit} (proses sayı, yaddaş, CPU, fayl ölçüsü limitləri) və
 *       {@code setpriv} (başqa istifadəçiyə keçid, əlavə hüquq alma qadağası) ilə sarılır. Alt proses tətbiqin
 *       {@code /proc/PID/environ} faylını oxuya bilmir, çünki o, root-a məxsusdur.</li>
 *   <li><b>Hər yoxlamaya ayrıca uid</b> ({@link #lease}): uid-lər hovuzdan götürülür ({@code uid-pool-size}); yoxlama bitəndə
 *       həmin uid-in BÜTÜN prosesləri öldürülür (fork-bombanın və ya arxa fonda qalan uşaq proseslərin qarşısı) və uid
 *       hovuza qaytarılır. Beləcə paralel şagird prosesləri bir-birini görmür/öldürmür, proses limiti hər yoxlamaya aiddir.</li>
 * </ol>
 *
 * <p>Qalan risk: alt proses çıxış şəbəkəsinə malikdir (bax: deploy/README).</p>
 */
@Component
public class JudgeSandbox {

    private static final Logger log = LoggerFactory.getLogger(JudgeSandbox.class);

    /** Alt prosesə ötürülən yeganə mühit dəyişənləri (böyük/kiçik hərf fərqi nəzərə alınmır). */
    private static final Set<String> ENV_ALLOWLIST = Set.of("PATH", "SYSTEMROOT", "WINDIR", "TEMP", "TMP", "TMPDIR");

    /** Kompilyasiya mərhələsi icra mərhələsindən genişdir: g++ (cc1plus) böyük yaddaş və bir neçə proses istifadə edir. */
    public enum Phase { COMPILE, RUN }

    private final boolean enabled;
    private final BlockingQueue<Integer> freeUids;
    private final long leaseTimeoutMillis;
    private final int maxProcesses;
    private final long maxMemoryBytes;
    private final int maxCpuSeconds;
    private final long maxFileBytes;
    // Cari thread-in icarəsi: JudgeService-in kompilyasiya/icra metodları ProcessBuilder qurarkən sandbox.wrap-i çağırır,
    // icarə isə yoxlamanın əvvəlində (try-with-resources) alınır — beləcə metod imzaları dəyişmir.
    private final ThreadLocal<Lease> current = new ThreadLocal<>();

    @Autowired
    public JudgeSandbox(
        @Value("${app.judge.sandbox.run-as-uid:}") String baseUid,
        @Value("${app.judge.sandbox.uid-pool-size:4}") int uidPoolSize,
        @Value("${app.judge.sandbox.lease-timeout-seconds:30}") long leaseTimeoutSeconds,
        @Value("${app.judge.sandbox.max-processes:64}") int maxProcesses,
        @Value("${app.judge.sandbox.max-memory-mb:1024}") long maxMemoryMb,
        @Value("${app.judge.sandbox.max-cpu-seconds:10}") int maxCpuSeconds,
        @Value("${app.judge.sandbox.max-file-mb:16}") long maxFileMb
    ) {
        String cleanUid = baseUid == null ? "" : baseUid.trim();
        // uid yalnız rəqəm ola bilər — komanda sətrinə başqa mətn düşməsin.
        if (!cleanUid.isEmpty() && !cleanUid.matches("\\d{1,9}")) {
            throw new IllegalArgumentException("app.judge.sandbox.run-as-uid rəqəm olmalıdır: " + cleanUid);
        }
        this.enabled = !cleanUid.isEmpty();
        int poolSize = Math.max(1, uidPoolSize);
        this.freeUids = new ArrayBlockingQueue<>(poolSize);
        if (enabled) {
            int base = Integer.parseInt(cleanUid);
            for (int i = 0; i < poolSize; i++) {
                freeUids.add(base + i);
            }
        }
        this.leaseTimeoutMillis = TimeUnit.SECONDS.toMillis(Math.max(0, leaseTimeoutSeconds));
        this.maxProcesses = maxProcesses;
        this.maxMemoryBytes = maxMemoryMb * 1024 * 1024;
        this.maxCpuSeconds = maxCpuSeconds;
        this.maxFileBytes = maxFileMb * 1024 * 1024;
    }

    /** Hüquq endirmə söndürülmüş nümunə (lokal inkişaf, testlər) — yalnız mühit təmizlənir. */
    public static JudgeSandbox disabled() {
        return new JudgeSandbox("", 1, 0, 64, 1024, 10, 16);
    }

    public boolean isEnabled() {
        return enabled;
    }

    /** Alt prosesin mühitini ağ siyahıya endirir və mesaj formatını sabitləyir. */
    public void prepare(ProcessBuilder pb) {
        Map<String, String> env = pb.environment();
        Map<String, String> kept = new HashMap<>();
        for (Map.Entry<String, String> e : env.entrySet()) {
            if (ENV_ALLOWLIST.contains(e.getKey().toUpperCase(Locale.ROOT))) {
                kept.put(e.getKey(), e.getValue());
            }
        }
        env.clear();
        env.putAll(kept);
        env.put("LC_ALL", "C");
    }

    /**
     * Bir yoxlama üçün ayrıca uid icarəyə götürür (hovuz boşdursa {@code lease-timeout-seconds} qədər gözləyir, sonra 503).
     * Cari thread-ə bağlanır; {@link Lease#close()} uid-in bütün proseslərini öldürüb uid-i qaytarır. Söndürülmüş
     * sandbox-da heç nə etməyən icarə qaytarılır.
     */
    public Lease lease() {
        return acquire(leaseTimeoutMillis);
    }

    /** {@link #lease()} kimi, amma gözləmir: uid yoxdursa {@code null} qaytarır (tez-tez çağırılan sintaksis yoxlaması üçün). */
    public Lease tryLease() {
        try {
            return acquire(0);
        } catch (ApiException busy) {
            return null;
        }
    }

    private Lease acquire(long timeoutMillis) {
        if (current.get() != null) {
            throw new IllegalStateException("Bu thread-də sandbox icarəsi artıq var (iç-içə yoxlama olmamalıdır)");
        }
        Integer uid = null;
        if (enabled) {
            try {
                uid = freeUids.poll(timeoutMillis, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (uid == null) {
                throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Yoxlayıcı hazırda məşğuldur, bir az sonra yenidən cəhd edin");
            }
        }
        Lease lease = new Lease(uid);
        current.set(lease);
        return lease;
    }

    /**
     * Komandanı cari icarənin uid-i ilə (açıqdırsa) limitlər və istifadəçi dəyişməsi ilə sarıyır; söndürülübsə olduğu kimi
     * qaytarır. Sandbox açıq olduqda icarəsiz çağırış İZİN VERİLMİR (sandbox-sız işə düşməsin deyə xəta verir).
     */
    public List<String> wrap(List<String> command, Phase phase) {
        if (!enabled) {
            return command;
        }
        Lease lease = current.get();
        if (lease == null || lease.uid == null) {
            throw new IllegalStateException("Sandbox açıqdır, amma bu thread-də icarə yoxdur — alt proses sandbox-sız başladıla bilməz");
        }
        boolean compile = phase == Phase.COMPILE;
        List<String> wrapped = new ArrayList<>();
        wrapped.add("prlimit");
        wrapped.add("--nproc=" + (compile ? Math.max(maxProcesses, 128) : maxProcesses));
        wrapped.add("--as=" + (compile ? Math.max(maxMemoryBytes, 2048L * 1024 * 1024) : maxMemoryBytes));
        wrapped.add("--cpu=" + (compile ? Math.max(maxCpuSeconds, 30) : maxCpuSeconds));
        wrapped.add("--fsize=" + (compile ? Math.max(maxFileBytes, 64L * 1024 * 1024) : maxFileBytes));
        wrapped.add("--core=0");
        wrapped.add("--");
        wrapped.addAll(asUser(lease.uid));
        wrapped.addAll(command);
        return wrapped;
    }

    /** {@code setpriv} sarğısı: uid/gid dəyişir, əlavə qrupları təmizləyir, yeni hüquq almağı qadağan edir. */
    private static List<String> asUser(int uid) {
        return List.of("setpriv", "--reuid=" + uid, "--regid=" + uid, "--clear-groups", "--no-new-privs", "--");
    }

    /**
     * Yoxlama qovluğunu hazırlayır: hüquq endirmə açıqdırsa, alt proses (başqa uid) orada kompilyasiya nəticəsini yaza
     * bilsin deyə qovluq hamı üçün yazıla bilən edilir. Qovluğun adı təsadüfi UUID-dir, üst qovluq isə siyahı verməyə
     * qadağandır ({@link #restrictParent}) — başqa şagirdin qovluğunu tapmaq mümkün deyil.
     */
    public void prepareWorkDir(Path workDir) throws IOException {
        if (!enabled) {
            return;
        }
        Files.setPosixFilePermissions(workDir, PosixFilePermissions.fromString("rwxrwxrwx"));
        Path source = workDir.resolve("main.cpp");
        if (Files.exists(source)) {
            Files.setPosixFilePermissions(source, PosixFilePermissions.fromString("rw-r--r--"));
        }
    }

    /** İş qovluğunun üstü yalnız "keçid" icazəsi verir (--x): daxilindəki UUID qovluqlar siyahıya düşmür. */
    public void restrictParent(Path workspaceDir) {
        if (!enabled) {
            return;
        }
        try {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwx--x--x");
            Files.setPosixFilePermissions(workspaceDir, perms);
        } catch (IOException | UnsupportedOperationException ignored) {
            // Siyahı qadağası əlavə qatdır; alınmasa da UUID adları təxmin edilə bilməz.
        }
    }

    /** Prosesi və bütün övladlarını öldürür (vaxt aşımı halında dərhal; qalıq təmizliyi {@link Lease#close()} edir). */
    public static void killTree(Process process) {
        process.descendants().forEach(ProcessHandle::destroyForcibly);
        process.destroyForcibly();
    }

    /** Bir yoxlamanın uid icarəsi. try-with-resources ilə istifadə olunur. */
    public final class Lease implements AutoCloseable {
        private final Integer uid; // söndürülmüş sandbox-da null

        private Lease(Integer uid) {
            this.uid = uid;
        }

        /** İcarə alınmış uid (söndürülmüşdürsə -1) — testlər və jurnal üçün. */
        public int uid() {
            return uid == null ? -1 : uid;
        }

        @Override
        public void close() {
            try {
                if (uid != null) {
                    killAllOf(uid);
                }
            } finally {
                current.remove();
                if (uid != null) {
                    freeUids.offer(uid);
                }
            }
        }
    }

    /** Verilmiş uid-in bütün proseslərini öldürür (fork-bomba və ya arxa fonda qalmış proqramlar). Xəta olsa yalnız jurnala yazılır. */
    private void killAllOf(int uid) {
        List<String> command = new ArrayList<>(asUser(uid));
        command.add("sh");
        command.add("-c");
        command.add("kill -9 -1");
        ProcessBuilder pb = new ProcessBuilder(command);
        prepare(pb);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        try {
            Process p = pb.start();
            p.getOutputStream().close();
            if (!p.waitFor(5, TimeUnit.SECONDS)) {
                p.destroyForcibly();
            }
        } catch (IOException e) {
            log.warn("Sandbox uid {} prosesləri təmizlənə bilmədi: {}", uid, e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
