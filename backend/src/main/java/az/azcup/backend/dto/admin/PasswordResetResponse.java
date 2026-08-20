package az.azcup.backend.dto.admin;

import java.util.Objects;

// Parol sıfırlamanın cavabı — YENİ (açıq mətn) parolu bir dəfəlik göstərir
// ki, admin onu istifadəçiyə çatdıra bilsin. Bazada yalnız bunun hash-i
// saxlanılır, açıq mətn heç yerdə saxlanılmır.
public class PasswordResetResponse {

    private final String newPassword;

    public PasswordResetResponse(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(newPassword);
    }

    @Override
    public String toString() {
        return "PasswordResetResponse{" +
            "newPassword='" + newPassword + '\'' +
            '}';
    }
}
