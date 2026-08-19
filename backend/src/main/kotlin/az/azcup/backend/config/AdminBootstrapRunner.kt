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
 * Tətbiq ilk dəfə başladılanda, hələ heç bir admin hesabı yoxdursa, avtomatik
 * bir admin hesabı yaradır — "admin yaratmaq üçün admin lazımdır" toyuq-yumurta
 * problemini həll edir (əks halda heç kim admin panelinə daxil ola bilməzdi).
 *
 * DİQQƏT: bu, YALNIZ bazada heç bir ADMIN rolunda istifadəçi olmadıqda işə düşür
 * (bax: run() metodundakı existsByRole yoxlaması). Yəni admin e-poçtu/parolunu
 * environment dəyişənləri ilə (ADMIN_EMAIL, ADMIN_PASSWORD) DƏYİŞDİRSƏNİZ, amma
 * bazada artıq bir admin sətri varsa, bu dəyişiklik YENİ admin YARADILMASINA
 * səbəb olmur — mövcud admin sətrini əl ilə yeniləmək lazımdır.
 */
@Component
class AdminBootstrapRunner(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(AdminBootstrapRunner::class.java)

    // Bu üç dəyər application.yml-dəki app.admin.* açarlarından gəlir, onlar da
    // öz növbəsində ADMIN_EMAIL/ADMIN_PASSWORD environment dəyişənlərindən oxunur
    // (placeholder default dəyərlərlə — production-da mütləq əvəz olunmalıdır).
    @Value("\${app.admin.email}")
    private lateinit var adminEmail: String

    @Value("\${app.admin.password}")
    private lateinit var adminPassword: String

    @Value("\${app.admin.full-name}")
    private lateinit var adminFullName: String

    // CommandLineRunner interfeysinin tələb etdiyi metod — Spring Boot tətbiq
    // tam yükləndikdən dərhal sonra bunu AVTOMATİK çağırır (əl ilə çağırmağa
    // ehtiyac yoxdur).
    override fun run(vararg args: String) {
        // Artıq bir admin varsa, heç nə etmə — təkrar yaratma.
        if (userRepository.existsByRole(Role.ADMIN)) {
            return
        }
        val admin = User(
            fullName = adminFullName,
            email = adminEmail,
            // Parol açıq mətn kimi YOX, BCrypt ilə hash-lənmiş halda saxlanılır.
            passwordHash = passwordEncoder.encode(adminPassword)!!,
            role = Role.ADMIN
        )
        userRepository.save(admin)
        log.info("Bootstrapped admin account: {}", adminEmail)
    }
}
