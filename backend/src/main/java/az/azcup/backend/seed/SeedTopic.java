package az.azcup.backend.seed;

import java.util.Objects;

// seed-data.json faylındakı bir mövzu sətrinin Java qarşılığı — Jackson
// (ObjectMapper) JSON-u birbaşa bu tipə deserializasiya edir (bax: SeedLoader).
// Topic entity-si ilə demək olar eynidir, amma ayrıca saxlanılıb ki, seed
// formatı ilə verilənlər bazası sxemi bir-birindən MÜSTƏQİL dəyişə bilsin.
public class SeedTopic {

    // Mövzunun URL-dostu slug-u.
    private final String slug;
    // Mövzunun göstərilmə sırası.
    private final int orderIndex;
    // Mövzunun başlığı.
    private final String title;
    // Qruplaşdırma etiketi (məs. "Ay 1").
    private final String monthTag;
    // Mövzunun qısa təsviri.
    private final String description;
    // Mövzunun dərc statusu.
    private final boolean published;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor —
    // Jackson JSON-dan deserializasiya edərkən bunu çağırır.
    public SeedTopic(String slug, int orderIndex, String title, String monthTag, String description, boolean published) {
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
    public int getOrderIndex() {
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

    // İki SeedTopic obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SeedTopic that = (SeedTopic) o;
        return orderIndex == that.orderIndex
            && published == that.published
            && Objects.equals(slug, that.slug)
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
        return "SeedTopic{" +
            "slug='" + slug + '\'' +
            ", orderIndex=" + orderIndex +
            ", title='" + title + '\'' +
            ", monthTag='" + monthTag + '\'' +
            ", description='" + description + '\'' +
            ", published=" + published +
            '}';
    }
}
