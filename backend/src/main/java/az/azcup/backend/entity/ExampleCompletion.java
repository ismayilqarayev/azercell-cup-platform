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
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

// Şagirdin bir kod nümunəsini düzgün yazıb tamamladığını göstərən qeyd.
// (nümunə, şagird) cütü unikaldır — təkrar yoxlama yeni sətir yaratmır.
@Entity
@Table(name = "example_completion", uniqueConstraints = @UniqueConstraint(columnNames = {"example_id", "student_id"}))
public class ExampleCompletion {

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
    private Instant completedAt;

    @PrePersist
    public void onCreate() {
        if (completedAt == null) {
            completedAt = Instant.now();
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

    public Instant getCompletedAt() {
        return completedAt;
    }
}
