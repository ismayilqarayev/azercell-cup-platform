package az.azcup.backend.dto.admin;

import az.azcup.backend.entity.Role;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

// Admin panelində bir istifadəçinin rolunu dəyişmək üçün
// ("PUT /api/admin/users/{id}/role").
public class RoleUpdateRequest {

    @NotNull
    private final Role role;

    public RoleUpdateRequest(Role role) {
        this.role = role;
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
        RoleUpdateRequest that = (RoleUpdateRequest) o;
        return role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(role);
    }

    @Override
    public String toString() {
        return "RoleUpdateRequest{" +
            "role=" + role +
            '}';
    }
}
