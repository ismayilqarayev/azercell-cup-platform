package az.azcup.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

// Sistemdəki bir istifadəçini (şagird, müəllim və ya admin) təmsil edən JPA
// entity-si. Cədvəl adı "app_user" seçilib (sadəcə "user" yox), çünki
// PostgreSQL-də "user" rezerv olunmuş açar sözdür və cədvəl adı kimi
// istifadə olunanda konfliktə səbəb olur.
@Entity
@Table(name = "app_user")
public class User {

    // Verilənlər bazasında avtomatik artırılan (IDENTITY) əsas açar.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // İstifadəçinin tam adı (ad və soyad).
    @Column(nullable = false)
    private String fullName = "";

    // E-poçt həm login üçün istifadə olunur, həm də unikal olmalıdır —
    // eyni e-poçtla iki hesab yaradıla bilməz (bax: unique = true).
    @Column(nullable = false, unique = true)
    private String email = "";

    // Əsl parol HEÇ VAXT saxlanılmır — yalnız BCrypt ilə hash-lənmiş forması.
    // Bax: AuthService (qeydiyyat zamanı hash-ləmə) və
    // CustomUserDetailsService (giriş zamanı yoxlama).
    @Column(nullable = false)
    private String passwordHash = "";

    // İstifadəçinin rolu (STUDENT/TEACHER/ADMIN) — bax: Role enum-u.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Müəllim hesabları admin tərəfindən təsdiqlənməyənə qədər "approved = false"
    // olur (bax: AdminController-dəki təsdiq endpoint-i). Şagirdlər qeydiyyatdan
    // dərhal sonra "approved = true" olur, əlavə təsdiqə ehtiyac yoxdur.
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean approved = true;

    // Admin bir istifadəçini silmək əvəzinə deaktiv edə bilər (active = false) —
    // bu, tarixçəni (təqdimatları və s.) itirmədən girişi bloklamaq üçündür.
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active = true;

    // İstifadəçinin qeydiyyatdan keçdiyi vaxt (avtomatik təyin olunur, dəyişdirilə bilməz).
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    // Hibernate obyekti bazaya yazmazdan (INSERT) əvvəl avtomatik çağırılır —
    // createdAt sahəsini əl ilə təyin etməyə ehtiyac qalmır.
    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // id sahəsinə yeni dəyər təyin edir.
    public void setId(Long id) {
        this.id = id;
    }

    // fullName sahəsinin dəyərini qaytarır.
    public String getFullName() {
        return fullName;
    }

    // fullName sahəsinə yeni dəyər təyin edir.
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // email sahəsinin dəyərini qaytarır.
    public String getEmail() {
        return email;
    }

    // email sahəsinə yeni dəyər təyin edir.
    public void setEmail(String email) {
        this.email = email;
    }

    // passwordHash sahəsinin dəyərini qaytarır.
    public String getPasswordHash() {
        return passwordHash;
    }

    // passwordHash sahəsinə yeni dəyər təyin edir.
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // role sahəsinin dəyərini qaytarır.
    public Role getRole() {
        return role;
    }

    // role sahəsinə yeni dəyər təyin edir.
    public void setRole(Role role) {
        this.role = role;
    }

    // approved sahəsinin dəyərini qaytarır. Boolean sahələr üçün getter adı
    // "get" əvəzinə "is" ilə başlayır — bu, JavaBeans konvensiyasıdır.
    public boolean isApproved() {
        return approved;
    }

    // approved sahəsinə yeni dəyər təyin edir.
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    // active sahəsinin dəyərini qaytarır.
    public boolean isActive() {
        return active;
    }

    // active sahəsinə yeni dəyər təyin edir.
    public void setActive(boolean active) {
        this.active = active;
    }

    // createdAt sahəsinin dəyərini qaytarır.
    public Instant getCreatedAt() {
        return createdAt;
    }

    // createdAt sahəsinə yeni dəyər təyin edir.
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
