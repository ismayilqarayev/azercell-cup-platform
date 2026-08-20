package az.azcup.backend.dto.auth;

import az.azcup.backend.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

// "POST /api/auth/register" gövdəsi. Parol üçün minimum 8 simvol tələbi
// @Size ilə tətbiq olunur. role sahəsi nullable-dır — AuthService-də,
// göndərilməyibsə default olaraq STUDENT təyin edilir (bax: AuthService.register).
public class RegisterRequest {

    // Qeydiyyatdan keçən istifadəçinin tam adı — boş ola bilməz.
    @NotBlank
    private final String fullName;

    // Qeydiyyat üçün e-poçt — boş ola bilməz və e-poçt formatında olmalıdır.
    @NotBlank
    @Email
    private final String email;

    // Açıq mətn parol — minimum 8, maksimum 100 simvol olmalıdır
    // (bax: AuthService-də BCrypt ilə hash-lənməsi).
    @NotBlank
    @Size(min = 8, max = 100)
    private final String password;

    // İstəyə bağlı rol — göndərilməzsə AuthService default olaraq STUDENT təyin edir.
    private final Role role;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public RegisterRequest(String fullName, String email, String password, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // fullName sahəsinin dəyərini qaytarır.
    public String getFullName() {
        return fullName;
    }

    // email sahəsinin dəyərini qaytarır.
    public String getEmail() {
        return email;
    }

    // password sahəsinin dəyərini qaytarır.
    public String getPassword() {
        return password;
    }

    // role sahəsinin dəyərini qaytarır.
    public Role getRole() {
        return role;
    }

    // İki RegisterRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegisterRequest that = (RegisterRequest) o;
        return Objects.equals(fullName, that.fullName)
            && Objects.equals(email, that.email)
            && Objects.equals(password, that.password)
            && role == that.role;
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(fullName, email, password, role);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "RegisterRequest{" +
            "fullName='" + fullName + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", role=" + role +
            '}';
    }
}
