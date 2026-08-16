package az.azcup.backend.config

import az.azcup.backend.entity.Role
import az.azcup.backend.entity.User
import az.azcup.backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

/**
 * Creates an initial admin account on first startup, if none exists yet —
 * solves the chicken-and-egg problem of needing an admin to create an admin.
 */
@Component
class AdminBootstrapRunner(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(AdminBootstrapRunner::class.java)

    @Value("\${app.admin.email}")
    private lateinit var adminEmail: String

    @Value("\${app.admin.password}")
    private lateinit var adminPassword: String

    @Value("\${app.admin.full-name}")
    private lateinit var adminFullName: String

    override fun run(vararg args: String) {
        if (userRepository.existsByRole(Role.ADMIN)) {
            return
        }
        val admin = User(
            fullName = adminFullName,
            email = adminEmail,
            passwordHash = passwordEncoder.encode(adminPassword)!!,
            role = Role.ADMIN
        )
        userRepository.save(admin)
        log.info("Bootstrapped admin account: {}", adminEmail)
    }
}
