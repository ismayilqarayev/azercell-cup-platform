package az.azcup.backend.dto.teacher;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

// "POST /api/teacher/groups/{id}/students" gövdəsi — müəllim şagirdi
// e-poçtu ilə (ID yox, çünki müəllim şagirdlərin ID-sini bilmir) axtarıb
// qrupa əlavə edir.
public class AddGroupMemberRequest {

    // Əlavə olunacaq şagirdin e-poçtu.
    @NotBlank
    @Email
    private final String email;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public AddGroupMemberRequest(String email) {
        this.email = email;
    }

    // email sahəsinin dəyərini qaytarır.
    public String getEmail() {
        return email;
    }

    // İki AddGroupMemberRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AddGroupMemberRequest that = (AddGroupMemberRequest) o;
        return Objects.equals(email, that.email);
    }

    // equals() ilə uyğun hash kodu yaradır.
    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    // Debug/log məqsədləri üçün obyektin mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "AddGroupMemberRequest{email='" + email + "'}";
    }
}
