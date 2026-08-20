package az.azcup.backend.dto.admin;

import az.azcup.backend.entity.Role;

import java.time.Instant;
import java.util.Objects;

// Admin panelinin istifadəçi idarəetmə ekranında (tək istifadəçinin tam
// görünüşü, rol dəyişmə, aktiv/deaktiv etmə) istifadə olunan tam forma.
public class AdminUserDetailDto {

    // İstifadəçinin verilənlər bazasındakı ID-si.
    private final Long id;
    // İstifadəçinin tam adı.
    private final String fullName;
    // İstifadəçinin e-poçt ünvanı.
    private final String email;
    // İstifadəçinin rolu.
    private final Role role;
    // İstifadəçinin (əsasən müəllim hesablarının) admin tərəfindən təsdiqlənib-təsdiqlənmədiyi.
    private final boolean approved;
    // İstifadəçinin hesabının aktiv olub-olmadığı.
    private final boolean active;
    // İstifadəçinin qeydiyyatdan keçdiyi vaxt.
    private final Instant createdAt;
    // İstifadəçinin uğurla (ACCEPTED statusu ilə) həll etdiyi problemlərin sayı.
    private final long totalSolved;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
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

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // fullName sahəsinin dəyərini qaytarır.
    public String getFullName() {
        return fullName;
    }

    // email sahəsinin dəyərini qaytarır.
    public String getEmail() {
        return email;
    }

    // role sahəsinin dəyərini qaytarır.
    public Role getRole() {
        return role;
    }

    // approved sahəsinin dəyərini qaytarır. Boolean sahə üçün getter "get"
    // əvəzinə "is" prefiksi ilə adlandırılıb (JavaBeans konvensiyası).
    public boolean isApproved() {
        return approved;
    }

    // active sahəsinin dəyərini qaytarır.
    public boolean isActive() {
        return active;
    }

    // createdAt sahəsinin dəyərini qaytarır.
    public Instant getCreatedAt() {
        return createdAt;
    }

    // totalSolved sahəsinin dəyərini qaytarır.
    public long getTotalSolved() {
        return totalSolved;
    }

    // İki AdminUserDetailDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
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

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, email, role, approved, active, createdAt, totalSolved);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
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
