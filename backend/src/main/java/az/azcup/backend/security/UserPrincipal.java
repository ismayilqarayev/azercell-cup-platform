package az.azcup.backend.security;

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

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    // Rol adını "ROLE_" prefiksi ilə qaytarır (məs. "ROLE_ADMIN") — Spring
    // Security-nin hasRole()/hasAnyRole() yoxlamaları məhz bu formatı gözləyir.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

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
