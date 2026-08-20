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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
