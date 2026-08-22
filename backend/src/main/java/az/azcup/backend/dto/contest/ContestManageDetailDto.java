package az.azcup.backend.dto.contest;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

// Tək bir yarışın TAM detalları — MÜƏLLİM/ADMİN idarəetmə panelinə
// göstərilən forma. ContestDetailDto-dan fərqi: problems sahəsi
// ContestProblemAdminDto (bütün test halları, gizli olanlar daxil) daşıyır,
// və "joined" sahəsi yoxdur (müəllim/admin yarışa qoşulmur, idarə edir).
public class ContestManageDetailDto {

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
    // Yarışın məsələləri, BÜTÜN test halları ilə birlikdə.
    private final List<ContestProblemAdminDto> problems;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ContestManageDetailDto(
        Long id,
        String title,
        String description,
        Instant startTime,
        Instant endTime,
        ContestStatusEnum status,
        List<ContestProblemAdminDto> problems
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
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

    // problems sahəsinin dəyərini qaytarır.
    public List<ContestProblemAdminDto> getProblems() {
        return problems;
    }

    // İki ContestManageDetailDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContestManageDetailDto that = (ContestManageDetailDto) o;
        return Objects.equals(id, that.id)
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
        return Objects.hash(id, title, description, startTime, endTime, status, problems);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ContestManageDetailDto{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", startTime=" + startTime +
            ", endTime=" + endTime +
            ", status=" + status +
            ", problems=" + problems +
            '}';
    }
}
