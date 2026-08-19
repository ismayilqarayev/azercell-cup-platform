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

// Sistemdəki bir istifadəçini (şagird, müəllim və ya admin) təmsil edən JPA
// entity-si. Cədvəl adı "app_user" seçilib (sadəcə "user" yox), çünki
// PostgreSQL-də "user" rezerv olunmuş açar sözdür və cədvəl adı kimi
// istifadə olunanda konfliktə səbəb olur.
@Entity
@Table(name = "app_user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var fullName: String = "",

    // E-poçt həm login üçün istifadə olunur, həm də unikal olmalıdır —
    // eyni e-poçtla iki hesab yaradıla bilməz (bax: unique = true).
    @Column(nullable = false, unique = true)
    var email: String = "",

    // Əsl parol HEÇ VAXT saxlanılmır — yalnız BCrypt ilə hash-lənmiş forması.
    // Bax: AuthService (qeydiyyat zamanı hash-ləmə) və
    // CustomUserDetailsService (giriş zamanı yoxlama).
    @Column(nullable = false)
    var passwordHash: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role? = null,

    // Müəllim hesabları admin tərəfindən təsdiqlənməyənə qədər "approved = false"
    // olur (bax: AdminController-dəki təsdiq endpoint-i). Şagirdlər qeydiyyatdan
    // dərhal sonra "approved = true" olur, əlavə təsdiqə ehtiyac yoxdur.
    @Column(nullable = false, columnDefinition = "boolean default true")
    var approved: Boolean = true,

    // Admin bir istifadəçini silmək əvəzinə deaktiv edə bilər (active = false) —
    // bu, tarixçəni (təqdimatları və s.) itirmədən girişi bloklamaq üçündür.
    @Column(nullable = false, columnDefinition = "boolean default true")
    var active: Boolean = true,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null
) {
    // Hibernate obyekti bazaya yazmazdan (INSERT) əvvəl avtomatik çağırılır —
    // createdAt sahəsini əl ilə təyin etməyə ehtiyac qalmır.
    @PrePersist
    fun onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now()
        }
    }
}
