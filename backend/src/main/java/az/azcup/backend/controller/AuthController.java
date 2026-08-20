package az.azcup.backend.controller;

import az.azcup.backend.dto.auth.AuthResponse;
import az.azcup.backend.dto.auth.LoginRequest;
import az.azcup.backend.dto.auth.RegisterRequest;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import az.azcup.backend.security.UserPrincipal;
import az.azcup.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

// Qeydiyyat, giriş və "mən kiməm" sorğusu — yeganə tam AÇIQ (login tələb
// etməyən) endpoint-lər /register və /login-dir (bax: SecurityConfig).
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // Frontend səhifə açılanda (yenilənəndə) hazırkı istifadəçinin kim
    // olduğunu bilmək üçün çağırır — token localStorage-da saxlanılır,
    // amma "bu token kimə aiddir və hələ etibarlıdırmı" sualının cavabı
    // yalnız server-dən gələ bilər.
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public MeResponse me(@AuthenticationPrincipal UserPrincipal principal) {
        User user = principal.getUser();
        return new MeResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }

    // Kiçik, yalnız bu endpoint-ə aid cavab tipi olduğu üçün ayrıca fayl
    // yaratmaq əvəzinə birbaşa controller daxilində təyin olunub.
    public static class MeResponse {

        private final Long id;
        private final String fullName;
        private final String email;
        private final Role role;

        public MeResponse(Long id, String fullName, String email, Role role) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
        }

        public Long getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public Role getRole() {
            return role;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MeResponse that = (MeResponse) o;
            return Objects.equals(id, that.id)
                && Objects.equals(fullName, that.fullName)
                && Objects.equals(email, that.email)
                && role == that.role;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, fullName, email, role);
        }

        @Override
        public String toString() {
            return "MeResponse{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
        }
    }
}
