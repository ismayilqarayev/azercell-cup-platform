package az.azcup.backend.dto.contest;

import az.azcup.backend.entity.SubmissionStatus;

import java.time.Instant;
import java.util.Objects;

// Bir yarış təqdimatının API-ya göstərilən forması — dərhal göndərmə
// cavabında və tarixçə siyahısında istifadə olunur. DİQQƏT: gizli test
// hallarının GİRİŞİ/GÖZLƏNİLƏN ÇIXIŞI bura HEÇ VAXT daxil edilmir — yalnız
// şagirdin ÖZ kodunun verdiyi stdout/stderr (bax: ContestSubmission-dakı
// eyni izah) və neçə test halının keçdiyi göstərilir.
public class ContestSubmissionResponse {

    // Təqdimatın verilənlər bazasındakı ID-si.
    private final Long id;
    // Təqdimatın aid olduğu yarış məsələsinin ID-si.
    private final Long contestProblemId;
    // Göndərilmiş mənbə kodu.
    private final String sourceCode;
    // Yoxlamanın ÜMUMİ nəticə statusu.
    private final SubmissionStatus status;
    // Neçə test halının keçdiyi.
    private final int passedTestCases;
    // Cəmi neçə test halı olduğu.
    private final int totalTestCases;
    // İlk uğursuz test halının sırası (hamısı keçibsə null).
    private final Integer firstFailedTestCaseOrder;
    // Şagirdin öz kodunun stdout çıxışı (ilk uğursuz testdə, ya da COMPILE_ERROR mesajı).
    private final String stdout;
    // Şagirdin öz kodunun stderr çıxışı.
    private final String stderr;
    // İcra müddəti millisaniyələrlə.
    private final Long executionTimeMs;
    // Bu cəhdə görə qazanılan bal (əvvəlcədən həll edilibsə 0 ola bilər).
    private final int pointsAwarded;
    // Təqdimatın göndərildiyi vaxt.
    private final Instant submittedAt;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ContestSubmissionResponse(
        Long id,
        Long contestProblemId,
        String sourceCode,
        SubmissionStatus status,
        int passedTestCases,
        int totalTestCases,
        Integer firstFailedTestCaseOrder,
        String stdout,
        String stderr,
        Long executionTimeMs,
        int pointsAwarded,
        Instant submittedAt
    ) {
        this.id = id;
        this.contestProblemId = contestProblemId;
        this.sourceCode = sourceCode;
        this.status = status;
        this.passedTestCases = passedTestCases;
        this.totalTestCases = totalTestCases;
        this.firstFailedTestCaseOrder = firstFailedTestCaseOrder;
        this.stdout = stdout;
        this.stderr = stderr;
        this.executionTimeMs = executionTimeMs;
        this.pointsAwarded = pointsAwarded;
        this.submittedAt = submittedAt;
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // contestProblemId sahəsinin dəyərini qaytarır.
    public Long getContestProblemId() {
        return contestProblemId;
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

    // pointsAwarded sahəsinin dəyərini qaytarır.
    public int getPointsAwarded() {
        return pointsAwarded;
    }

    // submittedAt sahəsinin dəyərini qaytarır.
    public Instant getSubmittedAt() {
        return submittedAt;
    }

    // İki ContestSubmissionResponse obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContestSubmissionResponse that = (ContestSubmissionResponse) o;
        return passedTestCases == that.passedTestCases
            && totalTestCases == that.totalTestCases
            && pointsAwarded == that.pointsAwarded
            && Objects.equals(id, that.id)
            && Objects.equals(contestProblemId, that.contestProblemId)
            && Objects.equals(sourceCode, that.sourceCode)
            && status == that.status
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
            id, contestProblemId, sourceCode, status, passedTestCases, totalTestCases,
            firstFailedTestCaseOrder, stdout, stderr, executionTimeMs, pointsAwarded, submittedAt
        );
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ContestSubmissionResponse{" +
            "id=" + id +
            ", contestProblemId=" + contestProblemId +
            ", sourceCode='" + sourceCode + '\'' +
            ", status=" + status +
            ", passedTestCases=" + passedTestCases +
            ", totalTestCases=" + totalTestCases +
            ", firstFailedTestCaseOrder=" + firstFailedTestCaseOrder +
            ", stdout='" + stdout + '\'' +
            ", stderr='" + stderr + '\'' +
            ", executionTimeMs=" + executionTimeMs +
            ", pointsAwarded=" + pointsAwarded +
            ", submittedAt=" + submittedAt +
            '}';
    }
}
