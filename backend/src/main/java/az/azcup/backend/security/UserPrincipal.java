package az.azcup.backend.security;

import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// Bizim User entity-mizi Spring Security-nin daxili UserDetails
// interfeysinə "bükən" (wrap edən) adapter. Controller-lərdə
// @AuthenticationPrincipal ilə əldə edilən obyekt məhz budur — .getUser()
// vasitəsilə əsl User entity-sinə çatmaq olur (bax: SubmissionController və s.).
public class UserPrincipal implements UserDetails {

    // Bu principal-ın "bükdüyü" əsl User entity-si.
    private final User user;

    // user sahəsini birbaşa təyin edən əsas (və yeganə) konstruktor.
    public UserPrincipal(User user) {
        this.user = user;
    }

    // user sahəsinin dəyərini qaytarır (əsl User entity-sinə çatmaq üçün).
    public User getUser() {
        return user;
    }

    // Hesab sistemdən istifadə edə bilərmi: aktivdir və (müəllimdirsə) admin tərəfindən təsdiqlənib.
    // AuthService.login ilə eyni qayda — JwtAuthFilter hər sorğuda yoxlayır ki, bloklanmış
    // hesabın əvvəlcədən alınmış tokeni də dərhal etibarsız olsun.
    public boolean canSignIn() {
        return user.isActive() && !(user.getRole() == Role.TEACHER && !user.isApproved());
    }

    // Rol adını "ROLE_" prefiksi ilə qaytarır (məs. "ROLE_ADMIN") — Spring
    // Security-nin hasRole()/hasAnyRole() yoxlamaları məhz bu formatı gözləyir.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    // Spring Security-nin login zamanı müqayisə etdiyi hash-lənmiş parolu qaytarır.
    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    // Bizdə "username" kimi e-poçt istifadə olunur.
    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
