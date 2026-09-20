package az.azcup.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

// Şagirdin kod nümunəsində etdiyi HƏR "Yoxla" cəhdi (vaxt, uyğunluq faizi, düzgün olub-olmaması).
// typedCode yalnız (nümunə, şagird) cütü üçün ƏN SON cəhddə saxlanılır — köhnə cəhdlərdə null-a
// çevrilir (bax: AssignmentService.checkExample), beləliklə cəhdlərin sayı dəqiq qalır, saxlanan kod isə məhdud olur.
@Entity
@Table(name = "example_attempt")
public class ExampleAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "example_id", nullable = false)
    private AssignmentExample example;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false, updatable = false)
    private Instant attemptedAt;

    @Column(nullable = false)
    private int percent;

    @Column(nullable = false)
    private boolean matched;

    @Column(columnDefinition = "text")
    private String typedCode;

    @PrePersist
    public void onCreate() {
        if (attemptedAt == null) {
            attemptedAt = Instant.now();
        }
    }

    public Long getId() {
        return id;
    }

    public AssignmentExample getExample() {
        return example;
    }

    public void setExample(AssignmentExample example) {
        this.example = example;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Instant getAttemptedAt() {
        return attemptedAt;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public String getTypedCode() {
        return typedCode;
    }

    public void setTypedCode(String typedCode) {
        this.typedCode = typedCode;
    }
}
