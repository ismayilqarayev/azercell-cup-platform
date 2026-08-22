package az.azcup.backend.judge;

import az.azcup.backend.entity.SubmissionStatus;

import java.util.Objects;

// JudgeService.judgeMultiple-in (bir kodu BİR NEÇƏ test halına qarşı
// yoxladıqdan sonra) qaytardığı ümumi nəticə. JudgeResult-dan fərqli olaraq
// (o, TƏK test halı üçündür) burada "neçə test keçdi", "hansı test halı
// ilk dəfə uğursuz oldu" kimi əlavə məlumat var — ContestSubmissionService
// bunları ContestSubmission sətrinə yazır.
public class MultiJudgeResult {

    // Ümumi nəticə: bütün testlər keçilibsə ACCEPTED, əks halda ilk
    // uğursuz testin səbəb olduğu status.
    private final SubmissionStatus status;
    // Neçə test halının uğurla keçildiyi (COMPILE_ERROR-da 0 qalır).
    private final int passedTestCases;
    // Cəmi neçə test halı olduğu.
    private final int totalTestCases;
    // İlk uğursuz test halının sırası — bütün testlər keçilibsə null.
    private final Integer firstFailedTestCaseOrder;
    // İlk uğursuz (və ya COMPILE_ERROR halında ümumi) stdout.
    private final String stdout;
    // İlk uğursuz (və ya COMPILE_ERROR halında ümumi) stderr.
    private final String stderr;
    // İcra edilmiş (short-circuit səbəbindən BƏLKƏ hamısı deyil) test
    // hallarının CƏMİ icra müddəti (millisaniyə).
    private final long executionTimeMs;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public MultiJudgeResult(
        SubmissionStatus status,
        int passedTestCases,
        int totalTestCases,
        Integer firstFailedTestCaseOrder,
        String stdout,
        String stderr,
        long executionTimeMs
    ) {
        this.status = status;
        this.passedTestCases = passedTestCases;
        this.totalTestCases = totalTestCases;
        this.firstFailedTestCaseOrder = firstFailedTestCaseOrder;
        this.stdout = stdout;
        this.stderr = stderr;
        this.executionTimeMs = executionTimeMs;
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
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    // İki MultiJudgeResult obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MultiJudgeResult that = (MultiJudgeResult) o;
        return passedTestCases == that.passedTestCases
            && totalTestCases == that.totalTestCases
            && executionTimeMs == that.executionTimeMs
            && status == that.status
            && Objects.equals(firstFailedTestCaseOrder, that.firstFailedTestCaseOrder)
            && Objects.equals(stdout, that.stdout)
            && Objects.equals(stderr, that.stderr);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(status, passedTestCases, totalTestCases, firstFailedTestCaseOrder, stdout, stderr, executionTimeMs);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "MultiJudgeResult{" +
            "status=" + status +
            ", passedTestCases=" + passedTestCases +
            ", totalTestCases=" + totalTestCases +
            ", firstFailedTestCaseOrder=" + firstFailedTestCaseOrder +
            ", stdout='" + stdout + '\'' +
            ", stderr='" + stderr + '\'' +
            ", executionTimeMs=" + executionTimeMs +
            '}';
    }
}
