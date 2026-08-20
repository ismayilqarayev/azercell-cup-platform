package az.azcup.backend.dto.admin;

import java.util.Objects;

// "POST /api/admin/users/{id}/reset-password" gövdəsi. newPassword
// göndərilməzsə (null), AdminService özü təsadüfi güclü parol yaradır
// (bax: AdminService.resetPassword) — admin istəyə görə ya öz parolunu
// təyin edə, ya da sistemə buraxa bilər.
public class PasswordResetRequest {

    private final String newPassword;

    public PasswordResetRequest(String newPassword) {
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
        PasswordResetRequest that = (PasswordResetRequest) o;
        return Objects.equals(newPassword, that.newPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newPassword);
    }

    @Override
    public String toString() {
        return "PasswordResetRequest{" +
            "newPassword='" + newPassword + '\'' +
            '}';
    }
}
