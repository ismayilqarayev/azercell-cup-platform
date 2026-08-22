package az.azcup.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

// Bir yarışı (Azercell Cup tərzi vaxt-məhdudlaşdırılmış müsabiqəni) təmsil
// edir. Yarışın statusu (gələcək/canlı/bitmiş) burada AYRICA sütun kimi
// SAXLANILMIR — startTime/endTime-a görə hər sorğuda HESABLANIR (bax:
// ContestService), ona görə status heç vaxt "köhnəlmiş" ola bilməz və ayrıca
// planlaşdırılmış (scheduled) tapşırığa ehtiyac qalmır.
@Entity
@Table(name = "contest")
public class Contest {

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Yarışın başlığı.
    @Column(nullable = false)
    private String title = "";

    // Yarışın qısa təsviri/qaydaları.
    @Column(length = 4000)
    private String description;

    // Yarışın başlama vaxtı. Bu vaxta qədər şagirdlər məsələləri görə bilmir
    // (bax: ContestService.getDetail-dəki görünürlük qaydası).
    @Column(nullable = false)
    private Instant startTime;

    // Yarışın bitmə vaxtı. Bu vaxtdan sonra göndərmə (submit) SERVER
    // TƏRƏFİNDƏ rədd olunur (bax: ContestSubmissionService.submit) — frontend
    // düyməni gizlətsə də, əsl qadağa burada tətbiq olunur.
    @Column(nullable = false)
    private Instant endTime;

    // Yarışın yaradıldığı vaxt (avtomatik təyin olunur, dəyişdirilə bilməz).
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Bax: User.onCreate() — eyni məntiq, sətir bazaya yazılmazdan əvvəl
    // vaxt möhürünü avtomatik təyin edir.
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

    // startTime sahəsinin dəyərini qaytarır.
    public Instant getStartTime() {
        return startTime;
    }

    // startTime sahəsinə yeni dəyər təyin edir.
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    // endTime sahəsinin dəyərini qaytarır.
    public Instant getEndTime() {
        return endTime;
    }

    // endTime sahəsinə yeni dəyər təyin edir.
    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
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
