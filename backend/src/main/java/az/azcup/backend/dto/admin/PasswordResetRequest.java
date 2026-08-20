package az.azcup.backend.dto.admin;

import java.util.Objects;

// "POST /api/admin/users/{id}/reset-password" gövdəsi. newPassword
// göndərilməzsə (null), AdminService özü təsadüfi güclü parol yaradır
// (bax: AdminService.resetPassword) — admin istəyə görə ya öz parolunu
// təyin edə, ya da sistemə buraxa bilər.
public class PasswordResetRequest {

    // Admin tərəfindən əl ilə təyin olunan yeni parol (null ola bilər —
    // bu halda təsadüfi parol yaradılır).
    private final String newPassword;

    // newPassword sahəsini birbaşa təyin edən əsas (və yeganə) konstruktor.
    public PasswordResetRequest(String newPassword) {
        this.newPassword = newPassword;
    }

    // newPassword sahəsinin dəyərini qaytarır.
    public String getNewPassword() {
        return newPassword;
    }

    // İki PasswordResetRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PasswordResetRequest that = (PasswordResetRequest) o;
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
        return "PasswordResetRequest{" +
            "newPassword='" + newPassword + '\'' +
            '}';
    }
}
