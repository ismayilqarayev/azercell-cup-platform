package az.azcup.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

// Bir şagirdin bir yarış məsələsinə göndərdiyi TƏK BİR kod təqdimatını
// təmsil edir. Adi Submission entity-sindən BİLƏRƏKDƏN ayrıdır — Submission.problem
// sətri NULL OLA BİLMƏYƏN əlaqədir, ona görə yarış təqdimatlarını da ora
// yazmaq ya problem sahəsini nullable etməyi (bütün mövcud sorğulara əlavə
// şərt tələb edir), ya da qarışıq XOR məntiqini tələb edərdi. Ayrıca cədvəl
// bunların heç birinə ehtiyac qoymur və "practice" statistikasını (ProgressDto
// və s.) yarış fəaliyyətindən tam təmiz saxlayır.
@Entity
@Table(name = "contest_submission")
public class ContestSubmission {

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bu təqdimatın aid olduğu yarış (contestProblem üzərindən də əldə
    // edilə bilər, amma reytinq cədvəli sorğularını asanlaşdırmaq üçün
    // burada da AYRICA saxlanılır — "denormallaşdırma").
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    // Bu təqdimatın aid olduğu konkret yarış məsələsi.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_problem_id", nullable = false)
    private ContestProblem contestProblem;

    // Bu təqdimatı göndərən şagird.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // @Lob — kod potensial olaraq uzun ola biləcəyi üçün.
    @Lob
    @Column(nullable = false)
    private String sourceCode = "";

    // Yoxlamanın ÜMUMİ nəticəsi — bütün test halları keçilibsə ACCEPTED,
    // əks halda ilk uğursuz test halının səbəb olduğu status (WRONG_ANSWER,
    // TIME_LIMIT_EXCEEDED, RUNTIME_ERROR və ya COMPILE_ERROR). Köhnə
    // SubmissionStatus enum-u yenidən istifadə olunur — praktika ilə eyni
    // status dəyərləri məntiqlidir, yarışa görə ayrıca enum yaratmağa ehtiyac yoxdur.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    // Neçə test halının UĞURLA keçildiyi (COMPILE_ERROR-da 0 qalır).
    @Column(nullable = false)
    private int passedTestCases = 0;

    // Bu məsələ üçün CƏMİ neçə test halı olduğu (şagirdə "3/5 keçdi" kimi
    // geribildirim vermək üçün).
    @Column(nullable = false)
    private int totalTestCases = 0;

    // Hansı test halının (sırası) İLK dəfə uğursuz olduğu — bütün test
    // halları keçilibsə null qalır. Şagirdə YALNIZ bu NÖMRƏ göstərilir,
    // gizli testin özü (girişi/gözlənilən çıxışı) HEÇ VAXT açılmır.
    private Integer firstFailedTestCaseOrder;

    // İlk uğursuz test halında proqramın verdiyi stdout — bu, şagirdin ÖZ
    // kodunun çıxışıdır (gizli testin gözlənilən cavabı deyil), ona görə
    // göstərilməsi təhlükəsizdir və şagirdə öz səhvini tapmaqda kömək edir.
    @Lob
    private String stdout;

    // İlk uğursuz test halında proqramın verdiyi stderr (xəta) çıxışı,
    // ya da kompilyasiya xətası mətni.
    @Lob
    private String stderr;

    // İcra edilmiş test hallarının CƏMİ icra müddəti (millisaniyə) — ilk
    // uğursuzluqdan sonra qalan testlər icra olunmadığı üçün "worst-case"
    // deyil, faktiki işlədilən vaxtın cəmidir.
    private Long executionTimeMs;

    // Bu təqdimatın nəticəsində qazanılan bal. Yalnız bu məsələ bu istifadəçi
    // tərəfindən İLK dəfə tam həll edildikdə ContestProblem.points-ə bərabər
    // olur, əks halda (artıq həll edilibsə və ya bu cəhd uğursuzdursa) 0-dır
    // (bax: ContestSubmissionService — "hamısı ya heç nə" bal siyasəti).
    @Column(nullable = false)
    private int pointsAwarded = 0;

    // Təqdimatın göndərildiyi vaxt (avtomatik təyin olunur, dəyişdirilə bilməz).
    @Column(nullable = false, updatable = false)
    private Instant submittedAt;

    // Bax: User.onCreate() — eyni məntiq, sətir bazaya yazılmazdan əvvəl
    // vaxt möhürünü avtomatik təyin edir.
    @PrePersist
    public void onCreate() {
        if (submittedAt == null) {
            submittedAt = Instant.now();
        }
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // id sahəsinə yeni dəyər təyin edir.
    public void setId(Long id) {
        this.id = id;
    }

    // contest sahəsinin dəyərini qaytarır.
    public Contest getContest() {
        return contest;
    }

    // contest sahəsinə yeni dəyər təyin edir.
    public void setContest(Contest contest) {
        this.contest = contest;
    }

    // contestProblem sahəsinin dəyərini qaytarır.
    public ContestProblem getContestProblem() {
        return contestProblem;
    }

    // contestProblem sahəsinə yeni dəyər təyin edir.
    public void setContestProblem(ContestProblem contestProblem) {
        this.contestProblem = contestProblem;
    }

    // user sahəsinin dəyərini qaytarır.
    public User getUser() {
        return user;
    }

    // user sahəsinə yeni dəyər təyin edir.
    public void setUser(User user) {
        this.user = user;
    }

    // sourceCode sahəsinin dəyərini qaytarır.
    public String getSourceCode() {
        return sourceCode;
    }

    // sourceCode sahəsinə yeni dəyər təyin edir.
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    // status sahəsinin dəyərini qaytarır.
    public SubmissionStatus getStatus() {
        return status;
    }

    // status sahəsinə yeni dəyər təyin edir.
    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    // passedTestCases sahəsinin dəyərini qaytarır.
    public int getPassedTestCases() {
        return passedTestCases;
    }

    // passedTestCases sahəsinə yeni dəyər təyin edir.
    public void setPassedTestCases(int passedTestCases) {
        this.passedTestCases = passedTestCases;
    }

    // totalTestCases sahəsinin dəyərini qaytarır.
    public int getTotalTestCases() {
        return totalTestCases;
    }

    // totalTestCases sahəsinə yeni dəyər təyin edir.
    public void setTotalTestCases(int totalTestCases) {
        this.totalTestCases = totalTestCases;
    }

    // firstFailedTestCaseOrder sahəsinin dəyərini qaytarır.
    public Integer getFirstFailedTestCaseOrder() {
        return firstFailedTestCaseOrder;
    }

    // firstFailedTestCaseOrder sahəsinə yeni dəyər təyin edir.
    public void setFirstFailedTestCaseOrder(Integer firstFailedTestCaseOrder) {
        this.firstFailedTestCaseOrder = firstFailedTestCaseOrder;
    }

    // stdout sahəsinin dəyərini qaytarır.
    public String getStdout() {
        return stdout;
    }

    // stdout sahəsinə yeni dəyər təyin edir.
    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    // stderr sahəsinin dəyərini qaytarır.
    public String getStderr() {
        return stderr;
    }

    // stderr sahəsinə yeni dəyər təyin edir.
    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    // executionTimeMs sahəsinin dəyərini qaytarır.
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    // executionTimeMs sahəsinə yeni dəyər təyin edir.
    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    // pointsAwarded sahəsinin dəyərini qaytarır.
    public int getPointsAwarded() {
        return pointsAwarded;
    }

    // pointsAwarded sahəsinə yeni dəyər təyin edir.
    public void setPointsAwarded(int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    // submittedAt sahəsinin dəyərini qaytarır.
    public Instant getSubmittedAt() {
        return submittedAt;
    }

    // submittedAt sahəsinə yeni dəyər təyin edir.
    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }
}
