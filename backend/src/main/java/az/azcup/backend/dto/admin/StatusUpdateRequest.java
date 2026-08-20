package az.azcup.backend.dto.admin;

import java.util.Objects;

// Admin panelində bir istifadəçini aktiv/deaktiv etmək üçün
// ("PUT /api/admin/users/{id}/status") — deaktiv istifadəçi giriş edə bilmir
// (bax: AuthService.login), amma tarixçəsi silinmir.
public class StatusUpdateRequest {

    // İstifadəçinin yeni aktivlik statusu (true — aktiv, false — deaktiv).
    private final boolean active;

    // active sahəsini birbaşa təyin edən əsas (və yeganə) konstruktor.
    public StatusUpdateRequest(boolean active) {
        this.active = active;
    }

    // active sahəsinin dəyərini qaytarır. Boolean sahə üçün getter "get"
    // əvəzinə "is" prefiksi ilə adlandırılıb (JavaBeans konvensiyası).
    public boolean isActive() {
        return active;
    }

    // İki StatusUpdateRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
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

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(active);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "StatusUpdateRequest{" +
            "active=" + active +
            '}';
    }
}
