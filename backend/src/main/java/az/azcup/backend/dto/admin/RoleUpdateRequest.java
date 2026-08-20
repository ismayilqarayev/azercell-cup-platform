package az.azcup.backend.dto.admin;

import az.azcup.backend.entity.Role;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

// Admin panelində bir istifadəçinin rolunu dəyişmək üçün
// ("PUT /api/admin/users/{id}/role").
public class RoleUpdateRequest {

    // İstifadəçiyə təyin ediləcək yeni rol — mütləq göndərilməlidir.
    @NotNull
    private final Role role;

    // role sahəsini birbaşa təyin edən əsas (və yeganə) konstruktor.
    public RoleUpdateRequest(Role role) {
        this.role = role;
    }

    // role sahəsinin dəyərini qaytarır.
    public Role getRole() {
        return role;
    }

    // İki RoleUpdateRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
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

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(role);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "RoleUpdateRequest{" +
            "role=" + role +
            '}';
    }
}
