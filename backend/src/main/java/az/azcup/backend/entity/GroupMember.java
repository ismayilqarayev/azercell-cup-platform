package az.azcup.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;

import java.time.Instant;

// Bir şagirdin bir qrupa ÜZV olduğunu qeyd edir (many-to-many əlaqənin
// əlaqə cədvəli). unique(group_id, student_id) məhdudiyyəti eyni şagirdin
// eyni qrupa TƏKRAR əlavə edilməsinin qarşısını alır — bax: ContestParticipant,
// eyni naxış.
@Entity
@Table(
    name = "group_member",
    uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "student_id"})
)
public class GroupMember {

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Üzv olduğu qrup.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // Qrupa əlavə olunan şagird (role=STUDENT olan User).
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // Qrupa əlavə olunma vaxtı (avtomatik təyin olunur, dəyişdirilə bilməz).
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

    // group sahəsinin dəyərini qaytarır.
    public Group getGroup() {
        return group;
    }

    // group sahəsinə yeni dəyər təyin edir.
    public void setGroup(Group group) {
        this.group = group;
    }

    // student sahəsinin dəyərini qaytarır.
    public User getStudent() {
        return student;
    }

    // student sahəsinə yeni dəyər təyin edir.
    public void setStudent(User student) {
        this.student = student;
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
