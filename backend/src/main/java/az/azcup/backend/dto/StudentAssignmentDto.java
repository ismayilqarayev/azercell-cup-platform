package az.azcup.backend.dto;

import java.time.Instant;
import java.util.Objects;

// "GET /api/me/assignments" cavabında hər tapşırıq üçün bir sətir —
// şagirdin ÜZVÜ olduğu bütün qruplardakı tapşırıqları, öz irəliləyişi ilə
// birlikdə göstərir (bax: AssignmentService.listForStudent).
public class StudentAssignmentDto {

    // Tapşırığın verilənlər bazasındakı ID-si.
    private final Long id;
    // Tapşırığın aid olduğu qrupun adı (məs. "10-A sinfi").
    private final String groupName;
    // Tapşırığın əhatə etdiyi mövzunun slug-u (frontend-də həmin mövzu
    // səhifəsinə keçid üçün istifadə oluna bilər).
    private final String topicSlug;
    // Tapşırığın əhatə etdiyi mövzunun başlığı.
    private final String topicTitle;
    // Tapşırığın qısa başlığı.
    private final String title;
    // Müəllimin əlavə qeydi (boş ola bilər).
    private final String description;
    // Son tarix.
    private final Instant dueAt;
    // Şagirdin bu mövzuda həll etdiyi problem sayı.
    private final long solvedCount;
    // Mövzudakı ümumi problem sayı.
    private final long totalCount;
    // Son tarix keçib, amma şagird hələ bitirməyibsə true.
    private final boolean overdue;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public StudentAssignmentDto(
        Long id,
        String groupName,
        String topicSlug,
        String topicTitle,
        String title,
        String description,
        Instant dueAt,
        long solvedCount,
        long totalCount,
        boolean overdue
    ) {
        this.id = id;
        this.groupName = groupName;
        this.topicSlug = topicSlug;
        this.topicTitle = topicTitle;
        this.title = title;
        this.description = description;
        this.dueAt = dueAt;
        this.solvedCount = solvedCount;
        this.totalCount = totalCount;
        this.overdue = overdue;
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // groupName sahəsinin dəyərini qaytarır.
    public String getGroupName() {
        return groupName;
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

    // solvedCount sahəsinin dəyərini qaytarır.
    public long getSolvedCount() {
        return solvedCount;
    }

    // totalCount sahəsinin dəyərini qaytarır.
    public long getTotalCount() {
        return totalCount;
    }

    // overdue sahəsinin dəyərini qaytarır.
    public boolean isOverdue() {
        return overdue;
    }

    // İki StudentAssignmentDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StudentAssignmentDto that = (StudentAssignmentDto) o;
        return solvedCount == that.solvedCount
            && totalCount == that.totalCount
            && overdue == that.overdue
            && Objects.equals(id, that.id)
            && Objects.equals(groupName, that.groupName)
            && Objects.equals(topicSlug, that.topicSlug)
            && Objects.equals(topicTitle, that.topicTitle)
            && Objects.equals(title, that.title)
            && Objects.equals(description, that.description)
            && Objects.equals(dueAt, that.dueAt);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(id, groupName, topicSlug, topicTitle, title, description, dueAt, solvedCount, totalCount, overdue);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "StudentAssignmentDto{" +
            "id=" + id +
            ", groupName='" + groupName + '\'' +
            ", topicSlug='" + topicSlug + '\'' +
            ", topicTitle='" + topicTitle + '\'' +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", dueAt=" + dueAt +
            ", solvedCount=" + solvedCount +
            ", totalCount=" + totalCount +
            ", overdue=" + overdue +
            '}';
    }
}
