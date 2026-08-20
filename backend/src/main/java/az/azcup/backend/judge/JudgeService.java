package az.azcup.backend.judge;

import az.azcup.backend.entity.Problem;
import az.azcup.backend.entity.SubmissionStatus;
import az.azcup.backend.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Şagirdin göndərdiyi C++ kodunu compile edib, problemin nümunə giriş/çıxışına
 * qarşı icra edən mərkəzi servis — platformanın "judge" (yoxlayıcı) hissəsi.
 *
 * DİQQƏT (təhlükəsizlik): burada konteyner (Docker və s.) izolyasiyası
 * İSTİFADƏ OLUNMUR — compile olunan proqram sərbəst bir OS prosesi kimi,
 * tətbiqin öz istifadəçi hüquqları altında icra olunur. Yalnız vaxt limiti
 * (timeout) və çıxış ölçüsü limiti (max-output-bytes) ilə məhdudlaşdırılır.
 * Bu, LOKAL/ETİBARLI istifadə üçün qəbul edilə bilər, amma açıq internetə
 * çıxarılan production mühitdə icra addımı mütləq konteynerləşdirilmiş
 * (sandboxed) bir alternativlə əvəz olunmalıdır (bax: runAgainstInput/runNoComparison).
 */
@Service
public class JudgeService {

    private final Logger log = LoggerFactory.getLogger(JudgeService.class);

    // g++ kompilyatorunun yolu (adətən sadəcə "g++", PATH-də olduğu güman edilir).
    private final String gppPath;
    // Hər yoxlama üçün müvəqqəti fayllar (main.cpp, main.exe) buraya yazılır.
    private final Path workspaceDir;
    private final long compileTimeoutSeconds;
    private final long runTimeoutSeconds;
    // Çox uzun kodların server resurslarını tükətməsinin qarşısını almaq üçün limit.
    private final int maxSourceLength;
    // Sonsuz dövrdə çap edən proqramların yaddaşı doldurmasının qarşısını alır.
    private final int maxOutputBytes;

    public JudgeService(
        @Value("${app.judge.gpp-path}") String gppPath,
        @Value("${app.judge.workspace-dir}") String workspaceDirPath,
        @Value("${app.judge.compile-timeout-seconds}") long compileTimeoutSeconds,
        @Value("${app.judge.run-timeout-seconds}") long runTimeoutSeconds,
        @Value("${app.judge.max-source-length}") int maxSourceLength,
        @Value("${app.judge.max-output-bytes}") int maxOutputBytes
    ) {
        this.gppPath = gppPath;
        this.workspaceDir = Paths.get(workspaceDirPath);
        this.compileTimeoutSeconds = compileTimeoutSeconds;
        this.runTimeoutSeconds = runTimeoutSeconds;
        this.maxSourceLength = maxSourceLength;
        this.maxOutputBytes = maxOutputBytes;
        // Servis yaradılanda (Spring tərəfindən) iş qovluğunun mövcud olduğuna əmin ol.
        try {
            Files.createDirectories(this.workspaceDir);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // Konkret bir PROBLEMƏ qarşı yoxlama — SubmissionService bunu çağırır.
    // Nəticə problem.exampleOutput ilə müqayisə edilərək ACCEPTED/WRONG_ANSWER
    // qərarı verir (bax: runAgainstInput).
    public JudgeResult judge(String sourceCode, Problem problem) {
        if (sourceCode == null || sourceCode.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Kod boş ola bilməz");
        }
        if (sourceCode.length() > maxSourceLength) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Kod çox uzundur (maksimum " + maxSourceLength + " simvol)");
        }

        // Hər yoxlama üçün TƏSADÜFİ AD-lı ayrıca qovluq — paralel sorğular
        // bir-birinin fayllarını üst-üstə yazmasın deyə (izolyasiya).
        Path workDir = workspaceDir.resolve(UUID.randomUUID().toString());
        try {
            Files.createDirectories(workDir);
            Path sourceFile = workDir.resolve("main.cpp");
            Files.writeString(sourceFile, sourceCode, StandardCharsets.UTF_8);
            Path binary = workDir.resolve("main.exe");

            CompileOutcome compileOutcome = compile(sourceFile, binary, workDir);
            if (!compileOutcome.isSuccess()) {
                return new JudgeResult(SubmissionStatus.COMPILE_ERROR, "", compileOutcome.getStderr(), 0);
            }

            return runAgainstInput(binary, workDir, problem.getExampleInput(), problem.getExampleOutput());
        } catch (IOException e) {
            log.error("Judge I/O error", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Yoxlama zamanı server xətası baş verdi");
        } finally {
            // Uğurlu/uğursuz — istənilən halda müvəqqəti faylları TƏMİZLƏ,
            // əks halda disk zamanla dolar.
            deleteRecursively(workDir);
        }
    }

    /**
     * İxtiyari kodu, çağıranın verdiyi stdin-ə qarşı compile edib icra edir —
     * gözlənilən çıxışla MÜQAYİSƏ YOXDUR. "Kod yazma sahəsi" (sərbəst
     * scratchpad) üçün istifadə olunur, çünki orada arxada konkret bir
     * {@link Problem} yoxdur, sadəcə "kodu işə sal və nəticəni göstər" tələb olunur.
     */
    public JudgeResult runFree(String sourceCode, String stdin) {
        if (sourceCode == null || sourceCode.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Kod boş ola bilməz");
        }
        if (sourceCode.length() > maxSourceLength) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Kod çox uzundur (maksimum " + maxSourceLength + " simvol)");
        }

        Path workDir = workspaceDir.resolve(UUID.randomUUID().toString());
        try {
            Files.createDirectories(workDir);
            Path sourceFile = workDir.resolve("main.cpp");
            Files.writeString(sourceFile, sourceCode, StandardCharsets.UTF_8);
            Path binary = workDir.resolve("main.exe");

            CompileOutcome compileOutcome = compile(sourceFile, binary, workDir);
            if (!compileOutcome.isSuccess()) {
                return new JudgeResult(SubmissionStatus.COMPILE_ERROR, "", compileOutcome.getStderr(), 0);
            }

            return runNoComparison(binary, workDir, stdin);
        } catch (IOException e) {
            log.error("Judge I/O error", e);
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "İcra zamanı server xətası baş verdi");
        } finally {
            deleteRecursively(workDir);
        }
    }

    // compile() metodunun nəticəsini daşıyan kiçik köməkçi tip.
    private static final class CompileOutcome {

        private final boolean success;
        private final String stderr;

        private CompileOutcome(boolean success, String stderr) {
            this.success = success;
            this.stderr = stderr;
        }

        boolean isSuccess() {
            return success;
        }

        String getStderr() {
            return stderr;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CompileOutcome that = (CompileOutcome) o;
            return success == that.success && Objects.equals(stderr, that.stderr);
        }

        @Override
        public int hashCode() {
            return Objects.hash(success, stderr);
        }

        @Override
        public String toString() {
            return "CompileOutcome{" +
                "success=" + success +
                ", stderr='" + stderr + '\'' +
                '}';
        }
    }

    // g++ ilə mənbə faylını binar-a çevirir. -O2 (optimallaşdırma) və
    // -std=c++17 (dil standartı) bayraqları istifadə olunur.
    private CompileOutcome compile(Path sourceFile, Path binary, Path workDir) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(
            gppPath, "-O2", "-std=c++17", "-o", binary.getFileName().toString(), sourceFile.getFileName().toString()
        );
        pb.directory(workDir.toFile());
        Process process = pb.start();

        // stdout/stderr oxunuşu ayrı thread-lərdə aparılır ki, proses öz
        // çıxış buferini doldurub "asılı" qalmasın (klassik ProcessBuilder tələsi:
        // əgər stdout/stderr oxunmasa, kiçik OS buferi dolur və proses özü bloklanır).
        CappedStreamReader stderrReader = CappedStreamReader.start(process.getErrorStream(), maxOutputBytes);
        // g++ stdin gözləmir, ona görə giriş axınını dərhal bağlayırıq.
        process.getOutputStream().close();
        CappedStreamReader stdoutReader = CappedStreamReader.start(process.getInputStream(), maxOutputBytes);

        boolean finished = waitQuietly(process, compileTimeoutSeconds);
        if (!finished) {
            process.destroyForcibly();
            return new CompileOutcome(false, "Kompilyasiya vaxtı bitdi (timeout)");
        }
        stdoutReader.join();
        stderrReader.join();
        if (process.exitValue() != 0) {
            // g++ xəta mesajlarını adətən stderr-ə yazır.
            return new CompileOutcome(false, stderrReader.result());
        }
        return new CompileOutcome(true, "");
    }

    // Binar-ı verilmiş girişlə (stdin) icra edir və çıxışı problemin
    // gözlənilən nəticəsi (expectedOutput) ilə müqayisə edərək
    // ACCEPTED/WRONG_ANSWER qərarı verir.
    private JudgeResult runAgainstInput(Path binary, Path workDir, String input, String expectedOutput) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(binary.toAbsolutePath().toString());
        pb.directory(workDir.toFile());
        Process process = pb.start();

        try (OutputStream stdin = process.getOutputStream()) {
            String stdinContent;
            if (input != null) {
                stdinContent = input;
            } else {
                stdinContent = "";
            }
            stdin.write(stdinContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
            // Proqram bütün girişi oxumadan bitə bilər — bu xəta deyil.
        }

        CappedStreamReader stdoutReader = CappedStreamReader.start(process.getInputStream(), maxOutputBytes);
        CappedStreamReader stderrReader = CappedStreamReader.start(process.getErrorStream(), maxOutputBytes);

        long start = System.currentTimeMillis();
        boolean finished = waitQuietly(process, runTimeoutSeconds);
        long elapsed = System.currentTimeMillis() - start;

        if (!finished) {
            // Vaxt bitdi (məs. sonsuz dövr) — məcburi dayandır.
            process.destroyForcibly();
            return new JudgeResult(SubmissionStatus.TIME_LIMIT_EXCEEDED, "", "", elapsed);
        }
        stdoutReader.join();
        stderrReader.join();

        String stdout = stdoutReader.result();
        String stderr = stderrReader.result();

        if (process.exitValue() != 0) {
            // Proqram sıfırdan fərqli kodla bitib — çöküş, seqmentasiya xətası və s.
            return new JudgeResult(SubmissionStatus.RUNTIME_ERROR, stdout, stderr, elapsed);
        }

        // normalize() sətir sonu simvollarını (\r\n vs \n) və sondakı boş
        // sətirləri/boşluqları ehmal edərək müqayisə edir — beləliklə
        // "eyni məzmun, fərqli format" halları səhv WRONG_ANSWER kimi qiymətləndirilmir.
        boolean matches = normalize(stdout).equals(normalize(expectedOutput));
        SubmissionStatus status;
        if (matches) {
            status = SubmissionStatus.ACCEPTED;
        } else {
            status = SubmissionStatus.WRONG_ANSWER;
        }
        return new JudgeResult(status, stdout, stderr, elapsed);
    }

    /**
     * runAgainstInput ilə eynidir, sadəcə gözlənilən çıxışla MÜQAYİSƏ YOXDUR —
     * burada ACCEPTED sadəcə "proqram uğurla icra olundu, çıxış kodu 0-dır"
     * mənasını daşıyır (düzgün NƏTİCƏ demək deyil, yalnız uğurlu İCRA deməkdir).
     */
    private JudgeResult runNoComparison(Path binary, Path workDir, String input) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(binary.toAbsolutePath().toString());
        pb.directory(workDir.toFile());
        Process process = pb.start();

        try (OutputStream stdin = process.getOutputStream()) {
            String stdinContent;
            if (input != null) {
                stdinContent = input;
            } else {
                stdinContent = "";
            }
            stdin.write(stdinContent.getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
            // Proqram bütün girişi oxumadan bitə bilər — bu xəta deyil.
        }

        CappedStreamReader stdoutReader = CappedStreamReader.start(process.getInputStream(), maxOutputBytes);
        CappedStreamReader stderrReader = CappedStreamReader.start(process.getErrorStream(), maxOutputBytes);

        long start = System.currentTimeMillis();
        boolean finished = waitQuietly(process, runTimeoutSeconds);
        long elapsed = System.currentTimeMillis() - start;

        if (!finished) {
            process.destroyForcibly();
            return new JudgeResult(SubmissionStatus.TIME_LIMIT_EXCEEDED, "", "", elapsed);
        }
        stdoutReader.join();
        stderrReader.join();

        String stdout = stdoutReader.result();
        String stderr = stderrReader.result();

        if (process.exitValue() != 0) {
            return new JudgeResult(SubmissionStatus.RUNTIME_ERROR, stdout, stderr, elapsed);
        }
        return new JudgeResult(SubmissionStatus.ACCEPTED, stdout, stderr, elapsed);
    }

    // Bir prosesin stdout/stderr axınını FONDA (ayrı thread-də) oxuyub
    // yaddaşda toplayan köməkçi sinif. maxBytes limitindən sonra gələn
    // baytlar sadəcə atılır (proses yenə də icra olunmağa davam edir,
    // amma çıxışı artıq yaddaşa yazılmır — yaddaş tükənməsinin qarşısı alınır).
    private static final class CappedStreamReader {
        private final Thread thread;

        @SuppressWarnings("this-escape")
        private volatile String result = "";

        private CappedStreamReader(InputStream inStream, int maxBytes) {
            this.thread = new Thread(() -> {
                try {
                    byte[] buf = new byte[4096];
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    int n;
                    while ((n = inStream.read(buf)) != -1) {
                        if (out.size() < maxBytes) {
                            out.write(buf, 0, Math.min(n, maxBytes - out.size()));
                        }
                    }
                    result = out.toString(StandardCharsets.UTF_8);
                } catch (IOException ignored) {
                    // Proses məcburi dayandırıldığı üçün stream bağlanıb — normaldır.
                }
            });
            // Daemon thread — əsas tətbiq bağlanarkən bu thread-lərin bitməsini
            // gözləməyə ehtiyac qalmır, JVM-i bloklamırlar.
            thread.setDaemon(true);
        }

        static CappedStreamReader start(InputStream inStream, int maxBytes) {
            CappedStreamReader reader = new CappedStreamReader(inStream, maxBytes);
            reader.thread.start();
            return reader;
        }

        // Thread-in bitməsini ən çoxu 2 saniyə gözləyir (uzun gözləməmək üçün
        // qısa timeout — proses artıq destroyForcibly() ilə öldürülübsə,
        // thread demək olar dərhal bitməlidir).
        void join() {
            try {
                thread.join(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        String result() {
            return result;
        }
    }

    // Prosesin bitməsini verilmiş saniyə qədər gözləyir; kəsilmə
    // (interrupt) halında prosesi məcburi öldürüb false qaytarır.
    private static boolean waitQuietly(Process process, long timeoutSeconds) {
        try {
            return process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
            return false;
        }
    }

    // İki mətni "praktik olaraq eyni" olub-olmadığını müəyyən etmək üçün
    // normallaşdırır: Windows/Unix sətir sonu fərqlərini (\r\n vs \n)
    // aradan qaldırır, hər sətrin sonundakı boşluqları və mətnin
    // sonundakı boş sətirləri atır. Bu olmasa, şagirdin düzgün cavabı
    // sadəcə əlavə boş sətirə görə WRONG_ANSWER ala bilərdi.
    private static String normalize(String s) {
        if (s == null) {
            return "";
        }
        String[] lines = s.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);
        int end = lines.length;
        while (end > 0 && lines[end - 1].strip().isEmpty()) {
            end--;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < end; i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(lines[i].stripTrailing());
        }
        return sb.toString();
    }

    // Bir yoxlamaya aid müvəqqəti qovluğu (main.cpp, main.exe və s.) tam
    // silir. Fayllar əvvəlcə silinməlidir ki, boşalan qovluqları silmək
    // mümkün olsun — ona görə Comparator.reverseOrder() ilə "ən dərin"
    // yoldan başlanır (əvvəl fayllar, sonra qovluqlar).
    private static void deleteRecursively(Path dir) {
        if (!Files.exists(dir)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(dir)) {
            List<Path> paths = new ArrayList<>();
            Iterator<Path> iterator = walk.iterator();
            while (iterator.hasNext()) {
                paths.add(iterator.next());
            }
            paths.sort(Comparator.reverseOrder());
            for (Path p : paths) {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException ignored) {
                }
            }
        } catch (IOException ignored) {
        }
    }
}
