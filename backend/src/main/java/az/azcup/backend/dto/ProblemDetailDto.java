package az.azcup.backend.dto;

import az.azcup.backend.entity.Difficulty;

import java.util.List;
import java.util.Objects;

// Tək bir problemin TAM detalları — "GET /api/problems/{id}" cavabı.
// Problem entity-sinin birbaşa özü qaytarılmır (bax: ProblemService) ki,
// internal sahələr (məs. referenceSolution — şagirdə göstərilməməli olan
// müəllim həlli) təsadüfən API cavabına sızmasın.
public class ProblemDetailDto {

    // Problemin verilənlər bazasındakı ID-si.
    private final Long id;
    // Problemin aid olduğu mövzunun slug-u.
    private final String topicSlug;
    // Mövzu daxilində problemin göstərilmə sırası.
    private final int orderIndex;
    // Problemin aid olduğu alt-qrup etiketi (ola bilər null).
    private final String subgroupLabel;
    // Problemin başlığı.
    private final String title;
    // Problemin çətinlik səviyyəsi.
    private final Difficulty difficulty;
    // Problemin mövzu etiketləri (məs. "massiv", "dp").
    private final List<String> tags;
    // Problemin tam mətni.
    private final String statement;
    // Giriş formatının izahı.
    private final String inputSpec;
    // Çıxış formatının izahı.
    private final String outputSpec;
    // Nümunə giriş.
    private final String exampleInput;
    // Nümunə girişə uyğun gözlənilən çıxış.
    private final String exampleOutput;
    // Həll yanaşmasının qısa izahı (ipucu).
    private final String approach;
    // Sorğunu edən istifadəçinin bu problemi artıq həll edib-etmədiyi —
    // hər istifadəçi üçün fərqli ola biləcəyindən Problem entity-sində
    // yox, məhz bu DTO-da hesablanaraq doldurulur.
    private final boolean solved;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ProblemDetailDto(
        Long id,
        String topicSlug,
        int orderIndex,
        String subgroupLabel,
        String title,
        Difficulty difficulty,
        List<String> tags,
        String statement,
        String inputSpec,
        String outputSpec,
        String exampleInput,
        String exampleOutput,
        String approach,
        boolean solved
    ) {
        this.id = id;
        this.topicSlug = topicSlug;
        this.orderIndex = orderIndex;
        this.subgroupLabel = subgroupLabel;
        this.title = title;
        this.difficulty = difficulty;
        this.tags = tags;
        this.statement = statement;
        this.inputSpec = inputSpec;
        this.outputSpec = outputSpec;
        this.exampleInput = exampleInput;
        this.exampleOutput = exampleOutput;
        this.approach = approach;
        this.solved = solved;
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // topicSlug sahəsinin dəyərini qaytarır.
    public String getTopicSlug() {
        return topicSlug;
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

    // statement sahəsinin dəyərini qaytarır.
    public String getStatement() {
        return statement;
    }

    // inputSpec sahəsinin dəyərini qaytarır.
    public String getInputSpec() {
        return inputSpec;
    }

    // outputSpec sahəsinin dəyərini qaytarır.
    public String getOutputSpec() {
        return outputSpec;
    }

    // exampleInput sahəsinin dəyərini qaytarır.
    public String getExampleInput() {
        return exampleInput;
    }

    // exampleOutput sahəsinin dəyərini qaytarır.
    public String getExampleOutput() {
        return exampleOutput;
    }

    // approach sahəsinin dəyərini qaytarır.
    public String getApproach() {
        return approach;
    }

    // solved sahəsinin dəyərini qaytarır. Boolean sahə üçün getter "get"
    // əvəzinə "is" prefiksi ilə adlandırılıb (JavaBeans konvensiyası).
    public boolean isSolved() {
        return solved;
    }

    // İki ProblemDetailDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProblemDetailDto that = (ProblemDetailDto) o;
        return orderIndex == that.orderIndex
            && solved == that.solved
            && Objects.equals(id, that.id)
            && Objects.equals(topicSlug, that.topicSlug)
            && Objects.equals(subgroupLabel, that.subgroupLabel)
            && Objects.equals(title, that.title)
            && difficulty == that.difficulty
            && Objects.equals(tags, that.tags)
            && Objects.equals(statement, that.statement)
            && Objects.equals(inputSpec, that.inputSpec)
            && Objects.equals(outputSpec, that.outputSpec)
            && Objects.equals(exampleInput, that.exampleInput)
            && Objects.equals(exampleOutput, that.exampleOutput)
            && Objects.equals(approach, that.approach);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(
            id, topicSlug, orderIndex, subgroupLabel, title, difficulty, tags,
            statement, inputSpec, outputSpec, exampleInput, exampleOutput, approach, solved
        );
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ProblemDetailDto{" +
            "id=" + id +
            ", topicSlug='" + topicSlug + '\'' +
            ", orderIndex=" + orderIndex +
            ", subgroupLabel='" + subgroupLabel + '\'' +
            ", title='" + title + '\'' +
            ", difficulty=" + difficulty +
            ", tags=" + tags +
            ", statement='" + statement + '\'' +
            ", inputSpec='" + inputSpec + '\'' +
            ", outputSpec='" + outputSpec + '\'' +
            ", exampleInput='" + exampleInput + '\'' +
            ", exampleOutput='" + exampleOutput + '\'' +
            ", approach='" + approach + '\'' +
            ", solved=" + solved +
            '}';
    }
}
