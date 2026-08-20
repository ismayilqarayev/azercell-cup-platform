package az.azcup.backend.dto.admin;

import java.util.Objects;

// Parol sıfırlamanın cavabı — YENİ (açıq mətn) parolu bir dəfəlik göstərir
// ki, admin onu istifadəçiyə çatdıra bilsin. Bazada yalnız bunun hash-i
// saxlanılır, açıq mətn heç yerdə saxlanılmır.
public class PasswordResetResponse {

    // İstifadəçiyə təyin olunan yeni, açıq mətnli parol.
    private final String newPassword;

    // newPassword sahəsini birbaşa təyin edən əsas (və yeganə) konstruktor.
    public PasswordResetResponse(String newPassword) {
        this.newPassword = newPassword;
    }

    // newPassword sahəsinin dəyərini qaytarır.
    public String getNewPassword() {
        return newPassword;
    }

    // İki PasswordResetResponse obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PasswordResetResponse that = (PasswordResetResponse) o;
        return Objects.equals(newPassword, that.newPassword);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(newPassword);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "PasswordResetResponse{" +
            "newPassword='" + newPassword + '\'' +
            '}';
    }
}
