package az.azcup.backend.repository

import az.azcup.backend.entity.Role
import az.azcup.backend.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun existsByRole(role: Role): Boolean
    fun findAllByRole(role: Role): List<User>
    fun countByRole(role: Role): Long
}
