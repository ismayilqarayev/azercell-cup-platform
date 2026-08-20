package az.azcup.backend.dto.admin;

import java.util.Objects;

// Admin panelində bir istifadəçini aktiv/deaktiv etmək üçün
// ("PUT /api/admin/users/{id}/status") — deaktiv istifadəçi giriş edə bilmir
// (bax: AuthService.login), amma tarixçəsi silinmir.
public class StatusUpdateRequest {

    private final boolean active;

    public StatusUpdateRequest(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StatusUpdateRequest that = (StatusUpdateRequest) o;
        return active == that.active;
    }

    @Override
    public int hashCode() {
        return Objects.hash(active);
    }

    @Override
    public String toString() {
        return "StatusUpdateRequest{" +
            "active=" + active +
            '}';
    }
}
