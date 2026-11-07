package az.azcup.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;

// Şagirdin bir testi həll etmə cəhdi. Başlayanda yaranır (startedAt serverin saatıdır, vaxt məhdudiyyəti ona görə hesablanır),
// təhvil verəndə submittedAt və bal yazılır. Cavablar yalnız təhvil zamanı saxlanılır (bax: QuizAnswer).
@Entity
@Table(name = "quiz_attempt", indexes = {
    @Index(name = "idx_quiz_attempt_student", columnList = "student_id"),
    @Index(name = "idx_quiz_attempt_quiz", columnList = "quiz_id")
})
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    private Instant startedAt;

    // null = hələ təhvil verilməyib.
    @Column
    private Instant submittedAt;

    @Column(nullable = false)
    private int correctCount;

    // Təhvil anında testdəki sual sayı (sonradan sual əlavə olunsa belə köhnə nəticə dəyişmir).
    @Column(nullable = false)
    private int totalCount;

    // Vaxt məhdudiyyəti keçəndən sonra (60 san. güzəştlə) təhvil verilib.
    @Column(nullable = false)
    private boolean late;

    public Long getId() {
        return id;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public boolean isLate() {
        return late;
    }

    public void setLate(boolean late) {
        this.late = late;
    }
}
