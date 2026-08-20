package az.azcup.backend.dto;

import az.azcup.backend.entity.SubmissionStatus;

import java.util.Objects;

// "POST /api/run" cavabı. status sahəsi burada ACCEPTED/WRONG_ANSWER
// mənasında YOX, sadəcə "uğurla icra oldu" (ACCEPTED) və ya
// COMPILE_ERROR/RUNTIME_ERROR/TIME_LIMIT_EXCEEDED kimi işlədilir
// (bax: JudgeService.runNoComparison).
public class RunResponse {

    private final SubmissionStatus status;
    private final String stdout;
    private final String stderr;
    private final long executionTimeMs;

    public RunResponse(SubmissionStatus status, String stdout, String stderr, long executionTimeMs) {
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
        RunResponse that = (RunResponse) o;
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
        return "RunResponse{" +
            "status=" + status +
            ", stdout='" + stdout + '\'' +
            ", stderr='" + stderr + '\'' +
            ", executionTimeMs=" + executionTimeMs +
            '}';
    }
}
