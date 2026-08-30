package az.azcup.backend.dto.teacher;

import java.time.Instant;
import java.util.Objects;

// Bir tapşırığın müəllim tərəfinə göstərilən forması — "Qruplarım" > qrup
// detalındakı tapşırıq siyahısında istifadə olunur.
public class AssignmentDto {

    // Tapşırığın verilənlər bazasındakı ID-si.
    private final Long id;
    // Tapşırığın aid olduğu qrupun ID-si.
    private final Long groupId;
    // Tapşırığın əhatə etdiyi mövzunun slug-u.
    private final String topicSlug;
    // Tapşırığın əhatə etdiyi mövzunun başlığı.
    private final String topicTitle;
    // Tapşırığın qısa başlığı.
    private final String title;
    // Müəllimin əlavə qeydi (boş ola bilər).
    private final String description;
    // Son tarix.
    private final Instant dueAt;
    // Mövzudakı ümumi problem sayı (bütün şagirdlər üçün eynidir).
    private final long totalProblems;
    // Tapşırığın yaradıldığı vaxt.
    private final Instant createdAt;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public AssignmentDto(
        Long id,
        Long groupId,
        String topicSlug,
        String topicTitle,
        String title,
        String description,
        Instant dueAt,
        long totalProblems,
        Instant createdAt
    ) {
        this.id = id;
        this.groupId = groupId;
        this.topicSlug = topicSlug;
        this.topicTitle = topicTitle;
        this.title = title;
        this.description = description;
        this.dueAt = dueAt;
        this.totalProblems = totalProblems;
        this.createdAt = createdAt;
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // groupId sahəsinin dəyərini qaytarır.
    public Long getGroupId() {
        return groupId;
    }

    // topicSlug sahəsinin dəyərini qaytarır.
    public String getTopicSlug() {
        return topicSlug;
    }

    // topicTitle sahəsinin dəyərini qaytarır.
    public String getTopicTitle() {
        return topicTitle;
    }

    // title sahəsinin dəyərini qaytarır.
    public String getTitle() {
        return title;
    }

    // description sahəsinin dəyərini qaytarır.
    public String getDescription() {
        return description;
    }

    // dueAt sahəsinin dəyərini qaytarır.
    public Instant getDueAt() {
        return dueAt;
    }

    // totalProblems sahəsinin dəyərini qaytarır.
    public long getTotalProblems() {
        return totalProblems;
    }

    // createdAt sahəsinin dəyərini qaytarır.
    public Instant getCreatedAt() {
        return createdAt;
    }

    // İki AssignmentDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AssignmentDto that = (AssignmentDto) o;
        return totalProblems == that.totalProblems
            && Objects.equals(id, that.id)
            && Objects.equals(groupId, that.groupId)
            && Objects.equals(topicSlug, that.topicSlug)
            && Objects.equals(topicTitle, that.topicTitle)
            && Objects.equals(title, that.title)
            && Objects.equals(description, that.description)
            && Objects.equals(dueAt, that.dueAt)
            && Objects.equals(createdAt, that.createdAt);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(id, groupId, topicSlug, topicTitle, title, description, dueAt, totalProblems, createdAt);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "AssignmentDto{" +
            "id=" + id +
            ", groupId=" + groupId +
            ", topicSlug='" + topicSlug + '\'' +
            ", topicTitle='" + topicTitle + '\'' +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", dueAt=" + dueAt +
            ", totalProblems=" + totalProblems +
            ", createdAt=" + createdAt +
            '}';
    }
}
