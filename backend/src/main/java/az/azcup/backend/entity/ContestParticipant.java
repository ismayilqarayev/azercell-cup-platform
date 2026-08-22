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

// Bir şagirdin bir yarışa QOŞULDUĞUNU qeyd edir. Şagird məsələ göndərmədən
// (submit) əvvəl əvvəlcə qoşulmalıdır (bax: ContestSubmissionService.submit) —
// bu, reytinq cədvəlində qoşulub-hələ-həll-etməyən şagirdləri 0 balla
// göstərməyə imkan verir (sadəcə heç görünməmək əvəzinə).
@Entity
@Table(
    name = "contest_participant",
    uniqueConstraints = @UniqueConstraint(columnNames = {"contest_id", "user_id"})
)
public class ContestParticipant {

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qoşulduğu yarış.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    // Qoşulan şagird. unique(contest_id, user_id) məhdudiyyəti eyni
    // şagirdin eyni yarışa TƏKRAR qoşulmasının qarşısını alır.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Qoşulma vaxtı (avtomatik təyin olunur, dəyişdirilə bilməz).
    @Column(nullable = false, updatable = false)
    private Instant joinedAt;

    // Bax: User.onCreate() — eyni məntiq, sətir bazaya yazılmazdan əvvəl
    // vaxt möhürünü avtomatik təyin edir.
    @PrePersist
    public void onCreate() {
        if (joinedAt == null) {
            joinedAt = Instant.now();
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

    // user sahəsinin dəyərini qaytarır.
    public User getUser() {
        return user;
    }

    // user sahəsinə yeni dəyər təyin edir.
    public void setUser(User user) {
        this.user = user;
    }

    // joinedAt sahəsinin dəyərini qaytarır.
    public Instant getJoinedAt() {
        return joinedAt;
    }

    // joinedAt sahəsinə yeni dəyər təyin edir.
    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }
}
