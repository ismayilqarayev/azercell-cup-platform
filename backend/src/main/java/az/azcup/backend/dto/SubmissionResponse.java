package az.azcup.backend.dto;

import az.azcup.backend.entity.SubmissionStatus;

import java.time.Instant;
import java.util.Objects;

// Bir kod təqdimatının (submission) API-ya göstərilən forması — həm dərhal
// göndərmə cavabında, həm də tarixçə siyahısında ("GET .../submissions")
// istifadə olunur.
public class SubmissionResponse {

    private final Long id;
    private final Long problemId;
    private final String sourceCode;
    private final SubmissionStatus status;
    private final String stdout;
    private final String stderr;
    private final Long executionTimeMs;
    private final Instant submittedAt;

    public SubmissionResponse(
        Long id,
        Long problemId,
        String sourceCode,
        SubmissionStatus status,
        String stdout,
        String stderr,
        Long executionTimeMs,
        Instant submittedAt
    ) {
        this.id = id;
        this.problemId = problemId;
        this.sourceCode = sourceCode;
        this.status = status;
        this.stdout = stdout;
        this.stderr = stderr;
        this.executionTimeMs = executionTimeMs;
        this.submittedAt = submittedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getProblemId() {
        return problemId;
    }

    public String getSourceCode() {
        return sourceCode;
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

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubmissionResponse that = (SubmissionResponse) o;
        return Objects.equals(id, that.id)
            && Objects.equals(problemId, that.problemId)
            && Objects.equals(sourceCode, that.sourceCode)
            && status == that.status
            && Objects.equals(stdout, that.stdout)
            && Objects.equals(stderr, that.stderr)
            && Objects.equals(executionTimeMs, that.executionTimeMs)
            && Objects.equals(submittedAt, that.submittedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, problemId, sourceCode, status, stdout, stderr, executionTimeMs, submittedAt);
    }

    @Override
    public String toString() {
        return "SubmissionResponse{" +
            "id=" + id +
            ", problemId=" + problemId +
            ", sourceCode='" + sourceCode + '\'' +
            ", status=" + status +
            ", stdout='" + stdout + '\'' +
            ", stderr='" + stderr + '\'' +
            ", executionTimeMs=" + executionTimeMs +
            ", submittedAt=" + submittedAt +
            '}';
    }
}
