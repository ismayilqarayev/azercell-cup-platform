package az.azcup.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

// "POST /api/auth/login" gövdəsi. @Email annotasiyası əsas format
// yoxlaması aparır (real e-poçt olub-olmadığını dəqiq təsdiqləmir,
// sadəcə "@" və domen olub-olmadığını yoxlayır) — əsl doğrulama
// AuthService-də parol/istifadəçi müqayisəsi ilə baş verir.
public class LoginRequest {

    // Giriş üçün istifadə olunan e-poçt — boş ola bilməz və e-poçt formatında olmalıdır.
    @NotBlank
    @Email
    private final String email;

    // Açıq mətn (hash-lənməmiş) parol — yalnız müqayisə üçün istifadə olunur, saxlanılmır.
    @NotBlank
    private final String password;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // email sahəsinin dəyərini qaytarır.
    public String getEmail() {
        return email;
    }

    // password sahəsinin dəyərini qaytarır.
    public String getPassword() {
        return password;
    }

    // İki LoginRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
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

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "LoginRequest{" +
            "email='" + email + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
