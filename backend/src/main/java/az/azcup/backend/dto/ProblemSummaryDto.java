package az.azcup.backend.dto;

import az.azcup.backend.entity.Difficulty;

import java.util.List;
import java.util.Objects;

// Bir mövzunun problem SİYAHISINDA göstərilən qısa forma (statement, io
// spesifikasiyaları və s. daxil deyil — bunlar yalnız ProblemDetailDto-da,
// konkret problem açılanda gətirilir ki, siyahı sorğusu yüngül qalsın).
public class ProblemSummaryDto {

    // Problemin verilənlər bazasındakı ID-si.
    private final Long id;
    // Mövzu daxilində problemin göstərilmə sırası.
    private final int orderIndex;
    // Problemin aid olduğu alt-qrup etiketi (ola bilər null).
    private final String subgroupLabel;
    // Problemin başlığı.
    private final String title;
    // Problemin çətinlik səviyyəsi.
    private final Difficulty difficulty;
    // Problemin mövzu etiketləri.
    private final List<String> tags;
    // Sorğunu edən istifadəçinin bu problemi artıq həll edib-etmədiyi.
    private final boolean solved;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ProblemSummaryDto(
        Long id,
        int orderIndex,
        String subgroupLabel,
        String title,
        Difficulty difficulty,
        List<String> tags,
        boolean solved
    ) {
        this.id = id;
        this.orderIndex = orderIndex;
        this.subgroupLabel = subgroupLabel;
        this.title = title;
        this.difficulty = difficulty;
        this.tags = tags;
        this.solved = solved;
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // orderIndex sahəsinin dəyərini qaytarır.
    public int getOrderIndex() {
        return orderIndex;
    }

    // subgroupLabel sahəsinin dəyərini qaytarır.
    public String getSubgroupLabel() {
        return subgroupLabel;
    }

    // title sahəsinin dəyərini qaytarır.
    public String getTitle() {
        return title;
    }

    // difficulty sahəsinin dəyərini qaytarır.
    public Difficulty getDifficulty() {
        return difficulty;
    }

    // tags sahəsinin dəyərini qaytarır.
    public List<String> getTags() {
        return tags;
    }

    // solved sahəsinin dəyərini qaytarır. Boolean sahə üçün getter "get"
    // əvəzinə "is" prefiksi ilə adlandırılıb (JavaBeans konvensiyası).
    public boolean isSolved() {
        return solved;
    }

    // İki ProblemSummaryDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProblemSummaryDto that = (ProblemSummaryDto) o;
        return orderIndex == that.orderIndex
            && solved == that.solved
            && Objects.equals(id, that.id)
            && Objects.equals(subgroupLabel, that.subgroupLabel)
            && Objects.equals(title, that.title)
            && difficulty == that.difficulty
            && Objects.equals(tags, that.tags);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(id, orderIndex, subgroupLabel, title, difficulty, tags, solved);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ProblemSummaryDto{" +
            "id=" + id +
            ", orderIndex=" + orderIndex +
            ", subgroupLabel='" + subgroupLabel + '\'' +
            ", title='" + title + '\'' +
            ", difficulty=" + difficulty +
            ", tags=" + tags +
            ", solved=" + solved +
            '}';
    }
}
