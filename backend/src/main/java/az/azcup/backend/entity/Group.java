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

// Bir müəllimin şagirdlərini bir yerə topladığı qrup (məs. "10-A sinfi",
// "Cümə qrupu"). Bir şagird bir neçə qrupda ola bilər (bax: GroupMember —
// many-to-many əlaqə), amma hər qrupun YALNIZ BİR sahib müəllimi var.
// Məqsəd: müəllimin admin panelindəki "bütün şagirdlər" siyahısı əvəzinə,
// yalnız öz qrupunun irəliləyişini görməsinə imkan vermək (bax: GroupService).
@Entity
@Table(name = "student_group")
public class Group {

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qrupun adı (məs. "10-A sinfi").
    @Column(nullable = false)
    private String name = "";

    // Qrupun sahib müəllimi. LAZY seçilib ki, qrup siyahısı çəkiləndə hər
    // dəfə müəllim sətri də avtomatik yüklənməsin (yalnız lazım olanda).
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    // Qrupun yaradıldığı vaxt (avtomatik təyin olunur, dəyişdirilə bilməz).
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

    // name sahəsinin dəyərini qaytarır.
    public String getName() {
        return name;
    }

    // name sahəsinə yeni dəyər təyin edir.
    public void setName(String name) {
        this.name = name;
    }

    // teacher sahəsinin dəyərini qaytarır.
    public User getTeacher() {
        return teacher;
    }

    // teacher sahəsinə yeni dəyər təyin edir.
    public void setTeacher(User teacher) {
        this.teacher = teacher;
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
