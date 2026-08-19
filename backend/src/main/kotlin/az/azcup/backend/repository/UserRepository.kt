package az.azcup.backend.repository

import az.azcup.backend.entity.Role
import az.azcup.backend.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    // Login zamanı e-poçta görə istifadəçini tapmaq üçün (CustomUserDetailsService).
    fun findByEmail(email: String): User?

    // Qeydiyyat zamanı e-poçtun artıq istifadə olunub-olunmadığını yoxlamaq üçün.
    fun existsByEmail(email: String): Boolean

    // İlk açılışda admin hesabının artıq mövcud olub-olmadığını yoxlamaq üçün
    // (bax: AdminBootstrapRunner) — "toqquşmadan qabaq" yoxlama.
    fun existsByRole(role: Role): Boolean

    // Admin panelində rola görə filtrlənmiş istifadəçi siyahısı üçün.
    fun findAllByRole(role: Role): List<User>

    // Rol üzrə istifadəçi sayını hesablamaq üçün (statistika məqsədilə).
    fun countByRole(role: Role): Long
}
