package az.azcup.backend.config;

import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import az.azcup.backend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
public class AdminBootstrapRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    // Admin hesabının mövcudluğunu yoxlamaq və yenisini yaratmaq üçün.
    private final UserRepository userRepository;
    // Admin parolunu bazaya yazmazdan əvvəl BCrypt ilə hash-ləmək üçün.
    private final PasswordEncoder passwordEncoder;

    // Bu üç dəyər application.yml-dəki app.admin.* açarlarından gəlir, onlar da
    // öz növbəsində ADMIN_EMAIL/ADMIN_PASSWORD environment dəyişənlərindən oxunur
    // (placeholder default dəyərlərlə — production-da mütləq əvəz olunmalıdır).
    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.full-name}")
    private String adminFullName;

    // Spring tərəfindən inject olunan asılılıqları (repository və şifrələyici) sahələrə təyin edir.
    public AdminBootstrapRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CommandLineRunner interfeysinin tələb etdiyi metod — Spring Boot tətbiq
    // tam yükləndikdən dərhal sonra bunu AVTOMATİK çağırır (əl ilə çağırmağa
    // ehtiyac yoxdur).
    @Override
    public void run(String... args) {
        // Artıq bir admin varsa, heç nə etmə — təkrar yaratma.
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }
        User admin = new User();
        admin.setFullName(adminFullName);
        admin.setEmail(adminEmail);
        // Parol açıq mətn kimi YOX, BCrypt ilə hash-lənmiş halda saxlanılır.
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        log.info("Bootstrapped admin account: {}", adminEmail);
    }
}
