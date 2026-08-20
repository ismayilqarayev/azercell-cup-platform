package az.azcup.backend.dto.admin;

import java.util.Objects;

// Admin panelinin mövzu CRUD ekranında istifadə olunan forma —
// problemCount/solvedCount kimi hesablanmış sahələr YOXDUR (TopicDto-dan
// fərqli olaraq), çünki bu, mövzunu redaktə etmək üçündür, statistika
// göstərmək üçün yox.
public class AdminTopicDto {

    // Mövzunun verilənlər bazasındakı ID-si.
    private final Long id;
    // Mövzunun URL-dostu slug-u.
    private final String slug;
    // Mövzunun roadmap-dakı göstərilmə sırası.
    private final int orderIndex;
    // Mövzunun başlığı.
    private final String title;
    // Qruplaşdırma etiketi (məs. "Ay 1").
    private final String monthTag;
    // Mövzunun qısa təsviri.
    private final String description;
    // Mövzunun dərc olunub-olunmadığı — bax: Topic.published.
    private final boolean published;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public AdminTopicDto(
        Long id,
        String slug,
        int orderIndex,
        String title,
        String monthTag,
        String description,
        boolean published
    ) {
        this.id = id;
        this.slug = slug;
        this.orderIndex = orderIndex;
        this.title = title;
        this.monthTag = monthTag;
        this.description = description;
        this.published = published;
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
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

    // İki AdminTopicDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdminTopicDto that = (AdminTopicDto) o;
        return orderIndex == that.orderIndex
            && published == that.published
            && Objects.equals(id, that.id)
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
        return Objects.hash(id, slug, orderIndex, title, monthTag, description, published);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "AdminTopicDto{" +
            "id=" + id +
            ", slug='" + slug + '\'' +
            ", orderIndex=" + orderIndex +
            ", title='" + title + '\'' +
            ", monthTag='" + monthTag + '\'' +
            ", description='" + description + '\'' +
            ", published=" + published +
            '}';
    }
}
