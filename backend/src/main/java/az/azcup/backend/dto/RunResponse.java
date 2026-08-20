package az.azcup.backend.dto;

import az.azcup.backend.entity.SubmissionStatus;

import java.util.Objects;

// "POST /api/run" cavabı. status sahəsi burada ACCEPTED/WRONG_ANSWER
// mənasında YOX, sadəcə "uğurla icra oldu" (ACCEPTED) və ya
// COMPILE_ERROR/RUNTIME_ERROR/TIME_LIMIT_EXCEEDED kimi işlədilir
// (bax: JudgeService.runNoComparison).
public class RunResponse {

    // İcranın nəticə statusu (bax: sinif üzərindəki qeyd).
    private final SubmissionStatus status;
    // Proqramın stdout çıxışı.
    private final String stdout;
    // Proqramın stderr (xəta) çıxışı.
    private final String stderr;
    // İcra müddəti millisaniyələrlə.
    private final long executionTimeMs;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public RunResponse(SubmissionStatus status, String stdout, String stderr, long executionTimeMs) {
        this.status = status;
        this.stdout = stdout;
        this.stderr = stderr;
        this.executionTimeMs = executionTimeMs;
    }

    // status sahəsinin dəyərini qaytarır.
    public SubmissionStatus getStatus() {
        return status;
    }

    // stdout sahəsinin dəyərini qaytarır.
    public String getStdout() {
        return stdout;
    }

    // stderr sahəsinin dəyərini qaytarır.
    public String getStderr() {
        return stderr;
    }

    // executionTimeMs sahəsinin dəyərini qaytarır.
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    // İki RunResponse obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
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

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(status, stdout, stderr, executionTimeMs);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
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
