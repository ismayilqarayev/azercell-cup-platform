package az.azcup.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

// "POST /api/auth/login" gövdəsi. @Email annotasiyası əsas format
// yoxlaması aparır (real e-poçt olub-olmadığını dəqiq təsdiqləmir,
// sadəcə "@" və domen olub-olmadığını yoxlayır) — əsl doğrulama
// AuthService-də parol/istifadəçi müqayisəsi ilə baş verir.
public class LoginRequest {

    @NotBlank
    @Email
    private final String email;

    @NotBlank
    private final String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LoginRequest that = (LoginRequest) o;
        return Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
            "email='" + email + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
