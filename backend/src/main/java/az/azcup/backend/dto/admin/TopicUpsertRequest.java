package az.azcup.backend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

// Mövzu yaratma/yeniləmə üçün ortaq gövdə (bax: ProblemUpsertRequest-dəki
// eyni "upsert" nümunəsi).
public class TopicUpsertRequest {

    // Mövzunun URL-dostu slug-u — boş ola bilməz.
    @NotBlank
    private final String slug;

    // Mövzunun göstərilmə sırası — mütləq göndərilməlidir.
    @NotNull
    private final Integer orderIndex;

    // Mövzunun başlığı — boş ola bilməz.
    @NotBlank
    private final String title;

    // Qruplaşdırma etiketi (istəyə bağlı).
    private final String monthTag;
    // Mövzunun qısa təsviri (istəyə bağlı).
    private final String description;
    // Mövzunun dərc statusu.
    private final boolean published;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public TopicUpsertRequest(
        String slug,
        Integer orderIndex,
        String title,
        String monthTag,
        String description,
        boolean published
    ) {
        this.slug = slug;
        this.orderIndex = orderIndex;
        this.title = title;
        this.monthTag = monthTag;
        this.description = description;
        this.published = published;
    }

    // slug sahəsinin dəyərini qaytarır.
    public String getSlug() {
        return slug;
    }

    // orderIndex sahəsinin dəyərini qaytarır.
    public Integer getOrderIndex() {
        return orderIndex;
    }

    // title sahəsinin dəyərini qaytarır.
    public String getTitle() {
        return title;
    }

    // monthTag sahəsinin dəyərini qaytarır.
    public String getMonthTag() {
        return monthTag;
    }

    // description sahəsinin dəyərini qaytarır.
    public String getDescription() {
        return description;
    }

    // published sahəsinin dəyərini qaytarır. Boolean sahə üçün getter "get"
    // əvəzinə "is" prefiksi ilə adlandırılıb (JavaBeans konvensiyası).
    public boolean isPublished() {
        return published;
    }

    // İki TopicUpsertRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TopicUpsertRequest that = (TopicUpsertRequest) o;
        return published == that.published
            && Objects.equals(slug, that.slug)
            && Objects.equals(orderIndex, that.orderIndex)
            && Objects.equals(title, that.title)
            && Objects.equals(monthTag, that.monthTag)
            && Objects.equals(description, that.description);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(slug, orderIndex, title, monthTag, description, published);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "TopicUpsertRequest{" +
            "slug='" + slug + '\'' +
            ", orderIndex=" + orderIndex +
            ", title='" + title + '\'' +
            ", monthTag='" + monthTag + '\'' +
            ", description='" + description + '\'' +
            ", published=" + published +
            '}';
    }
}
