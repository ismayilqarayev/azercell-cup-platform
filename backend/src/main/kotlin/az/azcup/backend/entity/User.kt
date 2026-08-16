package az.azcup.backend.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "app_user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var fullName: String = "",

    @Column(nullable = false, unique = true)
    var email: String = "",

    @Column(nullable = false)
    var passwordHash: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role? = null,

    @Column(nullable = false, columnDefinition = "boolean default true")
    var approved: Boolean = true,

    @Column(nullable = false, columnDefinition = "boolean default true")
    var active: Boolean = true,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null
) {
    @PrePersist
    fun onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now()
        }
    }
}
