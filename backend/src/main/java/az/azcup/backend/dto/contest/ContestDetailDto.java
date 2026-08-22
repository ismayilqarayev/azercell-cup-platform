package az.azcup.backend.dto.contest;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

// Tək bir yarışın TAM detalları — "GET /api/contests/{id}" cavabı,
// şagirdə göstərilən forma (problems sahəsi ContestProblemDto, yəni
// gizli test halları YOXDUR — bax: ContestManageDetailDto admin üçün).
public class ContestDetailDto {

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
    // Hesablanmış status.
    private final ContestStatusEnum status;
    // Sorğunu edən şagirdin bu yarışa artıq qoşulub-qoşulmadığı.
    private final boolean joined;
    // Yarışın məsələləri (yalnız startTime keçibsə/qoşulubsa dolu gəlir —
    // bax: ContestService.getDetail).
    private final List<ContestProblemDto> problems;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ContestDetailDto(
        Long id,
        String title,
        String description,
        Instant startTime,
        Instant endTime,
        ContestStatusEnum status,
        boolean joined,
        List<ContestProblemDto> problems
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.joined = joined;
        this.problems = problems;
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

    // joined sahəsinin dəyərini qaytarır.
    public boolean isJoined() {
        return joined;
    }

    // problems sahəsinin dəyərini qaytarır.
    public List<ContestProblemDto> getProblems() {
        return problems;
    }

    // İki ContestDetailDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContestDetailDto that = (ContestDetailDto) o;
        return joined == that.joined
            && Objects.equals(id, that.id)
            && Objects.equals(title, that.title)
            && Objects.equals(description, that.description)
            && Objects.equals(startTime, that.startTime)
            && Objects.equals(endTime, that.endTime)
            && status == that.status
            && Objects.equals(problems, that.problems);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, startTime, endTime, status, joined, problems);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ContestDetailDto{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", status=" + status +
            ", joined=" + joined +
            ", problems=" + problems +
            '}';
    }
}
