package az.azcup.backend.dto.contest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;

// "Upsert" — həm YARATMA (POST), həm YENİLƏMƏ (PUT) üçün eyni gövdə forması
// istifadə olunur (bax: dto.admin.ProblemUpsertRequest-dəki eyni konvensiya).
public class ContestUpsertRequest {

    // Yarışın başlığı — boş ola bilməz.
    @NotBlank
    private final String title;

    // Yarışın qısa təsviri (istəyə bağlı).
    private final String description;

    // Başlama vaxtı — mütləq göndərilməlidir.
    @NotNull
    private final Instant startTime;

    // Bitmə vaxtı — mütləq göndərilməlidir.
    @NotNull
    private final Instant endTime;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ContestUpsertRequest(String title, String description, Instant startTime, Instant endTime) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // title sahəsinin dəyərini qaytarır.
    public String getTitle() {
        return title;
    }

    // description sahəsinin dəyərini qaytarır.
    public String getDescription() {
        return description;
    }

    // startTime sahəsinin dəyərini qaytarır.
    public Instant getStartTime() {
        return startTime;
    }

    // endTime sahəsinin dəyərini qaytarır.
    public Instant getEndTime() {
        return endTime;
    }

    // İki ContestUpsertRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContestUpsertRequest that = (ContestUpsertRequest) o;
        return Objects.equals(title, that.title)
            && Objects.equals(description, that.description)
            && Objects.equals(startTime, that.startTime)
            && Objects.equals(endTime, that.endTime);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(title, description, startTime, endTime);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ContestUpsertRequest{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            '}';
    }
}
