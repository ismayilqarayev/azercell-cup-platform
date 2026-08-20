package az.azcup.backend.dto.admin;

import java.util.Objects;

// Müəllim panelindəki mövzu dərc/gizlətmə açar-düyməsi (toggle) üçün
// istifadə olunan minimal gövdə.
public class PublishUpdateRequest {

    // Mövzunun yeni dərc statusu (true — dərc olunub, false — gizlidir).
    private final boolean published;

    // published sahəsini birbaşa təyin edən əsas (və yeganə) konstruktor.
    public PublishUpdateRequest(boolean published) {
        this.published = published;
    }

    // published sahəsinin dəyərini qaytarır. Boolean sahə üçün getter "get"
    // əvəzinə "is" prefiksi ilə adlandırılıb (JavaBeans konvensiyası).
    public boolean isPublished() {
        return published;
    }

    // İki PublishUpdateRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PublishUpdateRequest that = (PublishUpdateRequest) o;
        return published == that.published;
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(published);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "PublishUpdateRequest{" +
            "published=" + published +
            '}';
    }
}
