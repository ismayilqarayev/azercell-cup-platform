package az.azcup.backend.dto.admin;

import java.time.Instant;
import java.util.Objects;

// Sadə istifadəçi siyahıları üçün (məs. "GET /api/admin/users" — şagird
// siyahısı, "GET /api/admin/pending-teachers" — təsdiq gözləyən müəllimlər).
// Rol/aktivlik/təsdiq statusu kimi ətraflı sahələr yoxdur — bunlar üçün
// AdminUserDetailDto istifadə olunur (bax: "/api/admin/users/{id}").
public class AdminUserDto {

    // İstifadəçinin verilənlər bazasındakı ID-si.
    private final Long id;
    // İstifadəçinin tam adı.
    private final String fullName;
    // İstifadəçinin e-poçt ünvanı.
    private final String email;
    // İstifadəçinin qeydiyyatdan keçdiyi vaxt.
    private final Instant createdAt;
    // İstifadəçinin uğurla həll etdiyi problemlərin sayı.
    private final long totalSolved;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public AdminUserDto(Long id, String fullName, String email, Instant createdAt, long totalSolved) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
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

    // createdAt sahəsinin dəyərini qaytarır.
    public Instant getCreatedAt() {
        return createdAt;
    }

    // totalSolved sahəsinin dəyərini qaytarır.
    public long getTotalSolved() {
        return totalSolved;
    }

    // İki AdminUserDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
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

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(id, fullName, email, createdAt, totalSolved);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
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
