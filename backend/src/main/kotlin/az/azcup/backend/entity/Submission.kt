package az.azcup.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import java.time.Instant

// Bir şagirdin bir problemə göndərdiyi TƏK BİR kod təqdimatını (submission)
// təmsil edir — JudgeService onu compile edib icra etdikdən sonra nəticə
// (status, stdout, stderr, icra vaxtı) bura yazılır. Şagirdin tarixçəsi və
// irəliləyişi (bax: ProgressDto) bu cədvəldəki sətirlərdən hesablanır.
@Entity
@Table(name = "submission")
class Submission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "problem_id", nullable = false)
    var problem: Problem? = null,

    // @Lob — kod potensial olaraq uzun ola biləcəyi üçün adi VARCHAR yerinə
    // "böyük obyekt" sütun tipi istifadə olunur (TEXT-ə uyğun gəlir).
    @Lob
    @Column(nullable = false)
    var sourceCode: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SubmissionStatus? = null,

    // Kompilyasiya uğursuz olarsa stdout boş qala bilər — ona görə nullable.
    @Lob
    var stdout: String? = null,

    @Lob
    var stderr: String? = null,

    var executionTimeMs: Long? = null,

    @Column(nullable = false, updatable = false)
    var submittedAt: Instant? = null
) {
    // Bax: User.onCreate() — eyni məntiq, sətir bazaya yazılmazdan əvvəl
    // vaxt möhürünü avtomatik təyin edir.
    @PrePersist
    fun onCreate() {
        if (submittedAt == null) {
            submittedAt = Instant.now()
        }
    }
}
