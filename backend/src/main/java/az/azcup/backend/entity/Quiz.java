package az.azcup.backend.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// DİM İnformatika bölməsindəki bir TEST: müəllim/admin yaradır, dərc edəndən sonra bütün şagirdlər həll edə bilir.
// Suallar test ilə birlikdə idarə olunur (cascade), ona görə redaktə bütöv bir sənəd kimi aparılır (bax: QuizAdminService).
@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Testin adı (məs. "Say sistemləri, test 1").
    @Column(nullable = false, length = 200)
    private String title = "";

    // Qısa izah (istəyə bağlı).
    @Column(columnDefinition = "text")
    private String description;

    // Mövzu etiketi (məs. "Python", "Say sistemləri"); siyahıda süzgəc kimi göstərilir.
    @Column(length = 80)
    private String category;

    // Vaxt məhdudiyyəti dəqiqə ilə; null = məhdudiyyət yoxdur. Server başlama vaxtını qeyd edir (bax: QuizAttempt).
    @Column
    private Integer timeLimitMinutes;

    // Yalnız dərc olunmuş testləri şagirdlər görür.
    @Column(nullable = false)
    private boolean published;

    // Testi yaradan müəllim/admin — redaktə və silmə yalnız ona (və admin-ə) açıqdır.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @BatchSize(size = 50)
    private List<QuizQuestion> questions = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }
}
