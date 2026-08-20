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

    @NotBlank
    private final String fullName;

    @NotBlank
    @Email
    private final String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private final String password;

    private final Role role;

    public RegisterRequest(String fullName, String email, String password, Role role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
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
        RegisterRequest that = (RegisterRequest) o;
        return Objects.equals(fullName, that.fullName)
            && Objects.equals(email, that.email)
            && Objects.equals(password, that.password)
            && role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, email, password, role);
    }

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
