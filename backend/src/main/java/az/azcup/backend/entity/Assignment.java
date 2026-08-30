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

// Bir müəllimin öz qrupuna verdiyi tapşırığı təmsil edir: "bu QRUPUN bu
// MÖVZUdakı bütün problemlərini bu TARIXƏ qədər həll et". Konkret problem
// seçimi YOXDUR (ayrıca join-cədvələ ehtiyac qalmır) — tapşırıq bütövlükdə
// bir Topic-ə aiddir, "neçəsi həll edilib" hesabı mövcud
// SubmissionRepository.solvedProblemIdsForUserInTopic ilə hesablanır (bax:
// AssignmentService.getGradebook/listForStudent).
@Entity
@Table(name = "assignment")
public class Assignment {

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Tapşırığın aid olduğu qrup — yalnız bu qrupun üzvləri görür.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // Tapşırığın əhatə etdiyi mövzu — "neçə problem, neçəsi həll edilib"
    // hesabı bu mövzunun problemlərinə görə aparılır.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    // Tapşırığın qısa başlığı (məs. "Mövzu 3 — bazar ertəsinə qədər").
    @Column(nullable = false)
    private String title = "";

    // Müəllimin əlavə qeydi (istəyə bağlı, boş ola bilər).
    @Column(columnDefinition = "text")
    private String description;

    // Son tarix — bundan sonra hələ bitirilməyən tapşırıq "gecikmiş" sayılır
    // (bax: AssignmentService-dəki overdue hesablaması).
    @Column(nullable = false)
    private Instant dueAt;

    // Tapşırığın yaradıldığı vaxt (avtomatik təyin olunur, dəyişdirilə bilməz).
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Hibernate obyekti bazaya yazmazdan (INSERT) əvvəl avtomatik çağırılır —
    // createdAt sahəsini əl ilə təyin etməyə ehtiyac qalmır.
    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
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

    // group sahəsinin dəyərini qaytarır.
    public Group getGroup() {
        return group;
    }

    // group sahəsinə yeni dəyər təyin edir.
    public void setGroup(Group group) {
        this.group = group;
    }

    // topic sahəsinin dəyərini qaytarır.
    public Topic getTopic() {
        return topic;
    }

    // topic sahəsinə yeni dəyər təyin edir.
    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    // title sahəsinin dəyərini qaytarır.
    public String getTitle() {
        return title;
    }

    // title sahəsinə yeni dəyər təyin edir.
    public void setTitle(String title) {
        this.title = title;
    }

    // description sahəsinin dəyərini qaytarır.
    public String getDescription() {
        return description;
    }

    // description sahəsinə yeni dəyər təyin edir.
    public void setDescription(String description) {
        this.description = description;
    }

    // dueAt sahəsinin dəyərini qaytarır.
    public Instant getDueAt() {
        return dueAt;
    }

    // dueAt sahəsinə yeni dəyər təyin edir.
    public void setDueAt(Instant dueAt) {
        this.dueAt = dueAt;
    }

    // createdAt sahəsinin dəyərini qaytarır.
    public Instant getCreatedAt() {
        return createdAt;
    }

    // createdAt sahəsinə yeni dəyər təyin edir.
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
