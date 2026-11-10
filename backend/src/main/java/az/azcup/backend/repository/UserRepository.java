package az.azcup.backend.repository;

import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    // Login zamanı e-poçta görə istifadəçini tapmaq üçün (CustomUserDetailsService).
    User findByEmail(String email);

    // Qeydiyyat zamanı e-poçtun artıq istifadə olunub-olunmadığını yoxlamaq üçün.
    boolean existsByEmail(String email);

    // İlk açılışda admin hesabının artıq mövcud olub-olmadığını yoxlamaq üçün
    // (bax: AdminBootstrapRunner) — "toqquşmadan qabaq" yoxlama.
    boolean existsByRole(Role role);

    // Admin panelində rola görə filtrlənmiş istifadəçi siyahısı üçün.
    List<User> findAllByRole(Role role);

    // Rol üzrə istifadəçi sayını hesablamaq üçün (statistika məqsədilə).
    long countByRole(Role role);
}
