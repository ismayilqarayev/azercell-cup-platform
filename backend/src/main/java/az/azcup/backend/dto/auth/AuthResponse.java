package az.azcup.backend.dto.auth;

import az.azcup.backend.entity.Role;

import java.util.Objects;

// Həm login, həm də register uğurlu olduqda qaytarılan cavab —
// frontend token-i localStorage-a yazır və sonrakı bütün sorğularda
// "Authorization: Bearer <token>" kimi göndərir.
public class AuthResponse {

    // Müəllim qeydiyyatdan keçib, hələ admin tərəfindən təsdiqlənməyibsə
    // token null ola bilər — hesab yaranıb, amma daxil olmaq mümkün deyil
    // (bax: AuthService.register — TEACHER rolunda approved=false).
    private final String token;

    // Giriş edən/qeydiyyatdan keçən istifadəçinin ID-si.
    private final Long userId;
    // İstifadəçinin tam adı.
    private final String fullName;
    // İstifadəçinin e-poçt ünvanı.
    private final String email;
    // İstifadəçinin rolu.
    private final Role role;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public AuthResponse(String token, Long userId, String fullName, String email, Role role) {
        this.token = token;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // token sahəsinin dəyərini qaytarır.
    public String getToken() {
        return token;
    }

    // userId sahəsinin dəyərini qaytarır.
    public Long getUserId() {
        return userId;
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

    // İki AuthResponse obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthResponse that = (AuthResponse) o;
        return Objects.equals(token, that.token)
            && Objects.equals(userId, that.userId)
            && Objects.equals(fullName, that.fullName)
            && Objects.equals(email, that.email)
            && role == that.role;
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(token, userId, fullName, email, role);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "AuthResponse{" +
            "token='" + token + '\'' +
            ", userId=" + userId +
            ", fullName='" + fullName + '\'' +
            ", email='" + email + '\'' +
            ", role=" + role +
            '}';
    }
}
