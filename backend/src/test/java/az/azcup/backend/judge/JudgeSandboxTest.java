package az.azcup.backend.judge;

import az.azcup.backend.exception.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// JudgeSandbox-un mühit təmizləməsini, komanda sarğısını (prlimit/setpriv) və uid icarələrini sınayır. Hüquq endirmənin özü
// (başqa uid ilə icra) yalnız Linux/root-da mümkündür — o, serverdə Docker-də ayrıca yoxlanılır (deploy/judge-selftest.sh).
class JudgeSandboxTest {

    // Sınaq üçün sandbox: baza uid 61000, hovuz pool, icarə gözləmə vaxtı 0 (hovuz doludursa dərhal 503).
    private JudgeSandbox sandbox(int pool) {
        return new JudgeSandbox("61000", pool, 0, 64, 512, 10, 16);
    }

    // İcarəni BAŞQA thread-də alır (icarə thread-ə bağlıdır; eyni thread-də iç-içə icarə qadağandır).
    private <T> T inOtherThread(Supplier<T> action) throws Exception {
        try {
            return CompletableFuture.supplyAsync(action).get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof Exception cause) {
                throw cause;
            }
            throw e;
        }
    }

    @Test
    void prepareKeepsOnlyAllowlistedEnvironment() {
        ProcessBuilder pb = new ProcessBuilder("g++");
        Map<String, String> env = pb.environment();
        env.clear();
        env.put("PATH", "/usr/bin");
        env.put("DB_PASSWORD", "neon-secret");
        env.put("JWT_SECRET", "jwt-secret");
        env.put("ADMIN_PASSWORD", "admin-secret");
        env.put("GITHUB_TOKEN", "ghp_secret");
        env.put("Temp", "C:\\Temp"); // Windows-da adlar fərqli hərflə ola bilər

        JudgeSandbox.disabled().prepare(pb);

        assertThat(pb.environment().keySet()).containsExactlyInAnyOrder("PATH", "Temp", "LC_ALL");
        assertThat(pb.environment()).containsEntry("PATH", "/usr/bin").containsEntry("LC_ALL", "C");
        assertThat(pb.environment().values()).noneMatch(v -> v.contains("secret"));
    }

    @Test
    void disabledSandboxLeavesCommandUntouched() {
        List<String> command = List.of("g++", "-O2", "main.cpp");
        JudgeSandbox disabled = JudgeSandbox.disabled();

        assertThat(disabled.isEnabled()).isFalse();
        try (JudgeSandbox.Lease lease = disabled.lease()) {
            assertThat(lease.uid()).isEqualTo(-1);
            assertThat(disabled.wrap(command, JudgeSandbox.Phase.COMPILE)).isEqualTo(command);
            assertThat(disabled.wrap(command, JudgeSandbox.Phase.RUN)).isEqualTo(command);
        }
    }

    @Test
    void enabledSandboxWrapsWithLimitsAndUserSwitch() {
        JudgeSandbox sandbox = new JudgeSandbox("61000", 2, 0, 64, 512, 10, 16);
        try (JudgeSandbox.Lease lease = sandbox.lease()) {
            List<String> run = sandbox.wrap(List.of("/work/main.exe"), JudgeSandbox.Phase.RUN);

            assertThat(sandbox.isEnabled()).isTrue();
            assertThat(lease.uid()).isEqualTo(61000);
            assertThat(run.get(0)).isEqualTo("prlimit");
            assertThat(run).contains("--nproc=64", "--as=" + 512L * 1024 * 1024, "--cpu=10", "--fsize=" + 16L * 1024 * 1024, "--core=0");
            // uid/gid icarənin uid-idir; hüquq alma qadağandır; əlavə qruplar təmizlənir.
            assertThat(run).containsSequence("setpriv", "--reuid=61000", "--regid=61000", "--clear-groups", "--no-new-privs", "--");
            // Əsl komanda ən sonda, dəyişməz qalır.
            assertThat(run.get(run.size() - 1)).isEqualTo("/work/main.exe");
        }
    }

    @Test
    void compilePhaseGetsRoomForTheCompiler() {
        JudgeSandbox sandbox = sandbox(1);
        try (JudgeSandbox.Lease ignored = sandbox.lease()) {
            List<String> compile = sandbox.wrap(List.of("g++", "main.cpp"), JudgeSandbox.Phase.COMPILE);

            // g++ (cc1plus + as + ld) icradan geniş limit tələb edir: ən az 128 proses, 2 GB, 30 san CPU, 64 MB fayl.
            assertThat(compile).contains("--nproc=128", "--as=" + 2048L * 1024 * 1024, "--cpu=30", "--fsize=" + 64L * 1024 * 1024);
        }
    }

    @Test
    void enabledSandboxRefusesToWrapWithoutLease() {
        // Fail-closed: icarəsiz çağırış sandbox-sız alt proses başlatmaq əvəzinə xəta verir.
        assertThatThrownBy(() -> sandbox(1).wrap(List.of("g++"), JudgeSandbox.Phase.COMPILE))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void concurrentLeasesGetDifferentUids() throws Exception {
        JudgeSandbox sandbox = sandbox(2);
        try (JudgeSandbox.Lease first = sandbox.lease()) {
            int second = inOtherThread(() -> {
                try (JudgeSandbox.Lease lease = sandbox.lease()) {
                    return lease.uid();
                }
            });
            assertThat(first.uid()).isEqualTo(61000);
            assertThat(second).isEqualTo(61001);
        }
    }

    @Test
    void exhaustedPoolAnswersServiceUnavailable() throws Exception {
        JudgeSandbox sandbox = sandbox(1);
        try (JudgeSandbox.Lease ignored = sandbox.lease()) {
            ApiException busy = inOtherThread(() -> {
                try (JudgeSandbox.Lease lease = sandbox.lease()) {
                    return null;
                } catch (ApiException e) {
                    return e;
                }
            });
            assertThat(busy).isNotNull();
            assertThat(busy.getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
            // tryLease gözləmir, sadəcə null qaytarır (sintaksis yoxlaması üçün).
            assertThat(inOtherThread(() -> sandbox.tryLease())).isNull();
        }
    }

    @Test
    void uidIsReusableAfterClose() {
        JudgeSandbox sandbox = sandbox(1);
        int first;
        try (JudgeSandbox.Lease lease = sandbox.lease()) {
            first = lease.uid();
        }
        try (JudgeSandbox.Lease lease = sandbox.lease()) {
            assertThat(lease.uid()).isEqualTo(first);
        }
    }

    @Test
    void nestedLeaseOnTheSameThreadIsRejected() {
        JudgeSandbox sandbox = sandbox(2);
        try (JudgeSandbox.Lease ignored = sandbox.lease()) {
            assertThatThrownBy(sandbox::lease).isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    void rejectsNonNumericUserIds() {
        assertThatThrownBy(() -> new JudgeSandbox("0; rm -rf /", 4, 0, 64, 512, 10, 16))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new JudgeSandbox("nobody", 4, 0, 64, 512, 10, 16))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
