package az.azcup.backend.dto.admin;

import java.time.Instant;
import java.util.Objects;

// Sadə istifadəçi siyahıları üçün (məs. "GET /api/admin/users" — şagird
// siyahısı, "GET /api/admin/pending-teachers" — təsdiq gözləyən müəllimlər).
// Rol/aktivlik/təsdiq statusu kimi ətraflı sahələr yoxdur — bunlar üçün
// AdminUserDetailDto istifadə olunur (bax: "/api/admin/users/{id}").
public class AdminUserDto {

    private final Long id;
    private final String fullName;
    private final String email;
    private final Instant createdAt;
    private final long totalSolved;

    public AdminUserDto(Long id, String fullName, String email, Instant createdAt, long totalSolved) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
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
        AdminUserDto that = (AdminUserDto) o;
        return totalSolved == that.totalSolved
            && Objects.equals(id, that.id)
            && Objects.equals(fullName, that.fullName)
            && Objects.equals(email, that.email)
            && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, email, createdAt, totalSolved);
    }

    @Override
    public String toString() {
        return "AdminUserDto{" +
            "id=" + id +
            ", fullName='" + fullName + '\'' +
            ", email='" + email + '\'' +
            ", createdAt=" + createdAt +
            ", totalSolved=" + totalSolved +
            '}';
    }
}
