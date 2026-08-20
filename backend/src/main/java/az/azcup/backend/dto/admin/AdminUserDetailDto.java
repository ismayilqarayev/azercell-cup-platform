package az.azcup.backend.dto.admin;

import az.azcup.backend.entity.Role;

import java.time.Instant;
import java.util.Objects;

// Admin panelinin istifadəçi idarəetmə ekranında (tək istifadəçinin tam
// görünüşü, rol dəyişmə, aktiv/deaktiv etmə) istifadə olunan tam forma.
public class AdminUserDetailDto {

    private final Long id;
    private final String fullName;
    private final String email;
    private final Role role;
    private final boolean approved;
    private final boolean active;
    private final Instant createdAt;
    private final long totalSolved;

    public AdminUserDetailDto(
        Long id,
        String fullName,
        String email,
        Role role,
        boolean approved,
        boolean active,
        Instant createdAt,
        long totalSolved
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.approved = approved;
        this.active = active;
        this.createdAt = createdAt;
        this.totalSolved = totalSolved;
    }

    public Long getId() {
        return id;
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

    public boolean isApproved() {
        return approved;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getTotalSolved() {
        return totalSolved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdminUserDetailDto that = (AdminUserDetailDto) o;
        return approved == that.approved
            && active == that.active
            && totalSolved == that.totalSolved
            && Objects.equals(id, that.id)
            && Objects.equals(fullName, that.fullName)
            && Objects.equals(email, that.email)
            && role == that.role
            && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, email, role, approved, active, createdAt, totalSolved);
    }

    @Override
    public String toString() {
        return "AdminUserDetailDto{" +
            "id=" + id +
            ", fullName='" + fullName + '\'' +
            ", email='" + email + '\'' +
            ", role=" + role +
            ", approved=" + approved +
            ", active=" + active +
            ", createdAt=" + createdAt +
            ", totalSolved=" + totalSolved +
            '}';
    }
}
