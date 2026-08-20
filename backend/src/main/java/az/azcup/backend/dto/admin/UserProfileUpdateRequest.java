package az.azcup.backend.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

// Admin panelində istifadəçinin ad/e-poçtunu redaktə etmək üçün
// ("PUT /api/admin/users/{id}") — parol və rol buraya daxil deyil,
// onlar üçün ayrıca endpoint-lər var (bax: RoleUpdateRequest,
// PasswordResetRequest).
public class UserProfileUpdateRequest {

    // İstifadəçinin yeni tam adı — boş ola bilməz.
    @NotBlank
    private final String fullName;

    // İstifadəçinin yeni e-poçt ünvanı — boş ola bilməz və düzgün e-poçt formatında olmalıdır.
    @NotBlank
    @Email
    private final String email;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public UserProfileUpdateRequest(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    // fullName sahəsinin dəyərini qaytarır.
    public String getFullName() {
        return fullName;
    }

    // email sahəsinin dəyərini qaytarır.
    public String getEmail() {
        return email;
    }

    // İki UserProfileUpdateRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserProfileUpdateRequest that = (UserProfileUpdateRequest) o;
        return Objects.equals(fullName, that.fullName) && Objects.equals(email, that.email);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(fullName, email);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "UserProfileUpdateRequest{" +
            "fullName='" + fullName + '\'' +
            ", email='" + email + '\'' +
            '}';
    }
}
