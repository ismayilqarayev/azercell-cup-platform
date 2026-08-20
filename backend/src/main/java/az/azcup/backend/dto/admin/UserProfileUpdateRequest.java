package az.azcup.backend.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

// Admin panelində istifadəçinin ad/e-poçtunu redaktə etmək üçün
// ("PUT /api/admin/users/{id}") — parol və rol buraya daxil deyil,
// onlar üçün ayrıca endpoint-lər var (bax: RoleUpdateRequest,
// PasswordResetRequest).
public class UserProfileUpdateRequest {

    @NotBlank
    private final String fullName;

    @NotBlank
    @Email
    private final String email;

    public UserProfileUpdateRequest(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(fullName, email);
    }

    @Override
    public String toString() {
        return "UserProfileUpdateRequest{" +
            "fullName='" + fullName + '\'' +
            ", email='" + email + '\'' +
            '}';
    }
}
