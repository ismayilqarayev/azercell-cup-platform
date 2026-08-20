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

    // Qeydiyyat/giriş məntiqinin faktiki icraçısı.
    private final AuthService authService;

    // Spring tərəfindən inject olunan AuthService-i sahəyə təyin edir.
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Yeni istifadəçi qeydiyyatdan keçirir və 201 CREATED statusu ilə token+profil cavabını qaytarır.
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    // E-poçt/parol ilə giriş edir və uğurlu olarsa JWT token qaytarır.
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

        // Giriş etmiş istifadəçinin ID-si.
        private final Long id;
        // İstifadəçinin tam adı.
        private final String fullName;
        // İstifadəçinin e-poçt ünvanı.
        private final String email;
        // İstifadəçinin rolu.
        private final Role role;

        // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
        public MeResponse(Long id, String fullName, String email, Role role) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
        }

        // id sahəsinin dəyərini qaytarır.
        public Long getId() {
            return id;
        }

        // fullName sahəsinin dəyərini qaytarır.
        public String getFullName() {
            return fullName;
        }

        // email sahəsinin dəyərini qaytarır.
        public String getEmail() {
            return email;
        }

        // role sahəsinin dəyərini qaytarır.
        public Role getRole() {
            return role;
        }

        // İki MeResponse obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
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

        // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
        // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
        // bütün sahələrin hash-lərini birləşdirir.
        @Override
        public int hashCode() {
            return Objects.hash(id, fullName, email, role);
        }

        // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
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
