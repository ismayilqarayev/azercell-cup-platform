package az.azcup.backend.dto.teacher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;

// "POST/PUT /api/teacher/groups/{groupId}/assignments" gövdəsi.
public class AssignmentUpsertRequest {

    // Tapşırığın qısa başlığı — boş ola bilməz.
    @NotBlank
    private final String title;

    // Müəllimin əlavə qeydi — istəyə bağlı, boş ola bilər.
    private final String description;

    // Tapşırığın əhatə etdiyi mövzunun slug-u — mütləq göndərilməlidir.
    @NotBlank
    private final String topicSlug;

    // Son tarix — mütləq göndərilməlidir.
    @NotNull
    private final Instant dueAt;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public AssignmentUpsertRequest(String title, String description, String topicSlug, Instant dueAt) {
        this.title = title;
        this.description = description;
        this.topicSlug = topicSlug;
        this.dueAt = dueAt;
    }

    // title sahəsinin dəyərini qaytarır.
    public String getTitle() {
        return title;
    }

    // description sahəsinin dəyərini qaytarır.
    public String getDescription() {
        return description;
    }

    // topicSlug sahəsinin dəyərini qaytarır.
    public String getTopicSlug() {
        return topicSlug;
    }

    // dueAt sahəsinin dəyərini qaytarır.
    public Instant getDueAt() {
        return dueAt;
    }

    // İki AssignmentUpsertRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AssignmentUpsertRequest that = (AssignmentUpsertRequest) o;
        return Objects.equals(title, that.title)
            && Objects.equals(description, that.description)
            && Objects.equals(topicSlug, that.topicSlug)
            && Objects.equals(dueAt, that.dueAt);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(title, description, topicSlug, dueAt);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "AssignmentUpsertRequest{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", topicSlug='" + topicSlug + '\'' +
            ", dueAt=" + dueAt +
            '}';
    }
}
