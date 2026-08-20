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

    private final Long userId;
    private final String fullName;
    private final String email;
    private final Role role;

    public AuthResponse(String token, Long userId, String fullName, String email, Role role) {
        this.token = token;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
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
        AuthResponse that = (AuthResponse) o;
        return Objects.equals(token, that.token)
            && Objects.equals(userId, that.userId)
            && Objects.equals(fullName, that.fullName)
            && Objects.equals(email, that.email)
            && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, userId, fullName, email, role);
    }

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
