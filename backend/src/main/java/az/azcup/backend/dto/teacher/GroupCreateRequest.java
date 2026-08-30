package az.azcup.backend.dto.teacher;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

// "POST /api/teacher/groups" və qrup adını dəyişmək üçün "PUT" gövdəsi.
public class GroupCreateRequest {

    // Qrupun adı — boş ola bilməz (məs. "10-A sinfi").
    @NotBlank
    private final String name;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public GroupCreateRequest(String name) {
        this.name = name;
    }

    // name sahəsinin dəyərini qaytarır.
    public String getName() {
        return name;
    }

    // İki GroupCreateRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupCreateRequest that = (GroupCreateRequest) o;
        return Objects.equals(name, that.name);
    }

    // equals() ilə uyğun hash kodu yaradır.
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    // Debug/log məqsədləri üçün obyektin mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "GroupCreateRequest{name='" + name + "'}";
    }
}
