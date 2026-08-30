package az.azcup.backend.dto;

import az.azcup.backend.entity.SubmissionStatus;

import java.time.Instant;
import java.util.Objects;

// Bir kod təqdimatının (submission) API-ya göstərilən forması — həm dərhal
// göndərmə cavabında, həm də tarixçə siyahısında ("GET .../submissions")
// istifadə olunur.
public class SubmissionResponse {

    // Təqdimatın verilənlər bazasındakı ID-si.
    private final Long id;
    // Təqdimatın aid olduğu problemin ID-si.
    private final Long problemId;
    // Göndərilmiş mənbə kodu.
    private final String sourceCode;
    // Yoxlamanın nəticə statusu.
    private final SubmissionStatus status;
    // Neçə test halının uğurla keçildiyi (nümunə + əlavə gizli testlər).
    private final int passedTestCases;
    // Cəmi neçə test halının yoxlandığı.
    private final int totalTestCases;
    // İlk uğursuz test halının sırası — hamısı keçilibsə null. Testin
    // özünün məzmunu (girişi/çıxışı) BURADA YOXDUR, yalnız nömrəsi.
    private final Integer firstFailedTestCaseOrder;
    // Proqramın stdout çıxışı.
    private final String stdout;
    // Proqramın stderr (xəta) çıxışı.
    private final String stderr;
    // İcra müddəti millisaniyələrlə (kompilyasiya uğursuzdursa null ola bilər).
    private final Long executionTimeMs;
    // Təqdimatın göndərildiyi vaxt.
    private final Instant submittedAt;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public SubmissionResponse(
        Long id,
        Long problemId,
        String sourceCode,
        SubmissionStatus status,
        int passedTestCases,
        int totalTestCases,
        Integer firstFailedTestCaseOrder,
        String stdout,
        String stderr,
        Long executionTimeMs,
        Instant submittedAt
    ) {
        this.id = id;
        this.problemId = problemId;
        this.sourceCode = sourceCode;
        this.status = status;
        this.passedTestCases = passedTestCases;
        this.totalTestCases = totalTestCases;
        this.firstFailedTestCaseOrder = firstFailedTestCaseOrder;
        this.stdout = stdout;
        this.stderr = stderr;
        this.executionTimeMs = executionTimeMs;
        this.submittedAt = submittedAt;
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // problemId sahəsinin dəyərini qaytarır.
    public Long getProblemId() {
        return problemId;
    }

    // sourceCode sahəsinin dəyərini qaytarır.
    public String getSourceCode() {
        return sourceCode;
    }

    // status sahəsinin dəyərini qaytarır.
    public SubmissionStatus getStatus() {
        return status;
    }

    // passedTestCases sahəsinin dəyərini qaytarır.
    public int getPassedTestCases() {
        return passedTestCases;
    }

    // totalTestCases sahəsinin dəyərini qaytarır.
    public int getTotalTestCases() {
        return totalTestCases;
    }

    // firstFailedTestCaseOrder sahəsinin dəyərini qaytarır.
    public Integer getFirstFailedTestCaseOrder() {
        return firstFailedTestCaseOrder;
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
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    // submittedAt sahəsinin dəyərini qaytarır.
    public Instant getSubmittedAt() {
        return submittedAt;
    }

    // İki SubmissionResponse obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
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
            && passedTestCases == that.passedTestCases
            && totalTestCases == that.totalTestCases
            && Objects.equals(firstFailedTestCaseOrder, that.firstFailedTestCaseOrder)
            && Objects.equals(stdout, that.stdout)
            && Objects.equals(stderr, that.stderr)
            && Objects.equals(executionTimeMs, that.executionTimeMs)
            && Objects.equals(submittedAt, that.submittedAt);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(
            id, problemId, sourceCode, status, passedTestCases, totalTestCases,
            firstFailedTestCaseOrder, stdout, stderr, executionTimeMs, submittedAt
        );
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "SubmissionResponse{" +
            "id=" + id +
            ", problemId=" + problemId +
            ", sourceCode='" + sourceCode + '\'' +
            ", status=" + status +
            ", passedTestCases=" + passedTestCases +
            ", totalTestCases=" + totalTestCases +
            ", firstFailedTestCaseOrder=" + firstFailedTestCaseOrder +
            ", stdout='" + stdout + '\'' +
            ", stderr='" + stderr + '\'' +
            ", executionTimeMs=" + executionTimeMs +
            ", submittedAt=" + submittedAt +
            '}';
    }
}
