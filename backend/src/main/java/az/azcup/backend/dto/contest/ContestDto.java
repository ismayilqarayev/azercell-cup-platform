package az.azcup.backend.dto.contest;

import java.time.Instant;
import java.util.Objects;

// Yarışların siyahısında ("GET /api/contests") göstərilən qısa forma —
// tam məsələ/test halı məlumatını daşımır (bax: ContestDetailDto).
public class ContestDto {

    // Yarışın verilənlər bazasındakı ID-si.
    private final Long id;
    // Yarışın başlığı.
    private final String title;
    // Yarışın qısa təsviri.
    private final String description;
    // Başlama vaxtı.
    private final Instant startTime;
    // Bitmə vaxtı.
    private final Instant endTime;
    // Hesablanmış status (UPCOMING/ACTIVE/ENDED).
    private final ContestStatusEnum status;
    // Bu yarışda neçə məsələ olduğu (siyahıda "N məsələ" kimi göstərmək üçün).
    private final int problemCount;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ContestDto(
        Long id,
        String title,
        String description,
        Instant startTime,
        Instant endTime,
        ContestStatusEnum status,
        int problemCount
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.problemCount = problemCount;
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
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

    // status sahəsinin dəyərini qaytarır.
    public ContestStatusEnum getStatus() {
        return status;
    }

    // problemCount sahəsinin dəyərini qaytarır.
    public int getProblemCount() {
        return problemCount;
    }

    // İki ContestDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContestDto that = (ContestDto) o;
        return problemCount == that.problemCount
            && Objects.equals(id, that.id)
            && Objects.equals(title, that.title)
            && Objects.equals(description, that.description)
            && Objects.equals(startTime, that.startTime)
            && Objects.equals(endTime, that.endTime)
            && status == that.status;
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, startTime, endTime, status, problemCount);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ContestDto{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", status=" + status +
            ", problemCount=" + problemCount +
            '}';
    }
}
