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

// Bir şagirdin bir problemə göndərdiyi TƏK BİR kod təqdimatını (submission)
// təmsil edir — JudgeService onu compile edib icra etdikdən sonra nəticə
// (status, stdout, stderr, icra vaxtı) bura yazılır. Şagirdin tarixçəsi və
// irəliləyişi (bax: ProgressDto) bu cədvəldəki sətirlərdən hesablanır.
@Entity
@Table(name = "submission")
public class Submission {

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bu təqdimatı göndərən istifadəçi.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Bu təqdimatın aid olduğu problem.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    // @Lob — kod potensial olaraq uzun ola biləcəyi üçün adi VARCHAR yerinə
    // "böyük obyekt" sütun tipi istifadə olunur (TEXT-ə uyğun gəlir).
    @Lob
    @Column(nullable = false)
    private String sourceCode = "";

    // Yoxlamanın nəticəsi (ACCEPTED, WRONG_ANSWER və s.) — bax: SubmissionStatus enum-u.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    // Kompilyasiya uğursuz olarsa stdout boş qala bilər — ona görə nullable.
    @Lob
    private String stdout;

    // Proqramın stderr (xəta) çıxışı — kompilyasiya və ya icra xətalarında dolur.
    @Lob
    private String stderr;

    // Kodun icra müddəti millisaniyələrlə (kompilyasiya uğursuz olduqda null qalır).
    private Long executionTimeMs;

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

    // user sahəsinin dəyərini qaytarır.
    public User getUser() {
        return user;
    }

    // user sahəsinə yeni dəyər təyin edir.
    public void setUser(User user) {
        this.user = user;
    }

    // problem sahəsinin dəyərini qaytarır.
    public Problem getProblem() {
        return problem;
    }

    // problem sahəsinə yeni dəyər təyin edir.
    public void setProblem(Problem problem) {
        this.problem = problem;
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

    // submittedAt sahəsinin dəyərini qaytarır.
    public Instant getSubmittedAt() {
        return submittedAt;
    }

    // submittedAt sahəsinə yeni dəyər təyin edir.
    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }
}
