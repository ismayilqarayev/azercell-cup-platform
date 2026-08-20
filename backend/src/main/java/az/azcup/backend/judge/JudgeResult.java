package az.azcup.backend.judge;

import az.azcup.backend.entity.SubmissionStatus;

import java.util.Objects;

// JudgeService-in bir kodu compile+icra etdikdən sonra qaytardığı nəticə.
// Bu obyekt həm SubmissionService (bazaya Submission kimi yazmaq üçün),
// həm də RunController (birbaşa HTTP cavabı kimi qaytarmaq üçün) tərəfindən
// istifadə olunur.
public class JudgeResult {

    private final SubmissionStatus status;
    private final String stdout;
    private final String stderr;
    private final long executionTimeMs;

    public JudgeResult(SubmissionStatus status, String stdout, String stderr, long executionTimeMs) {
        this.status = status;
        this.stdout = stdout;
        this.stderr = stderr;
        this.executionTimeMs = executionTimeMs;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JudgeResult that = (JudgeResult) o;
        return executionTimeMs == that.executionTimeMs
            && status == that.status
            && Objects.equals(stdout, that.stdout)
            && Objects.equals(stderr, that.stderr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, stdout, stderr, executionTimeMs);
    }

    @Override
    public String toString() {
        return "JudgeResult{" +
            "status=" + status +
            ", stdout='" + stdout + '\'' +
            ", stderr='" + stderr + '\'' +
            ", executionTimeMs=" + executionTimeMs +
            '}';
    }
}
