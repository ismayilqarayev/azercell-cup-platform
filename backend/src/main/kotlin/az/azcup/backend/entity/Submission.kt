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

    @Lob
    @Column(nullable = false)
    var sourceCode: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: SubmissionStatus? = null,

    @Lob
    var stdout: String? = null,

    @Lob
    var stderr: String? = null,

    var executionTimeMs: Long? = null,

    @Column(nullable = false, updatable = false)
    var submittedAt: Instant? = null
) {
    @PrePersist
    fun onCreate() {
        if (submittedAt == null) {
            submittedAt = Instant.now()
        }
    }
}
