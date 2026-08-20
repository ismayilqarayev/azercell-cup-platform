package az.azcup.backend.dto.admin;

import az.azcup.backend.entity.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

// "Upsert" — həm YARATMA (POST), həm YENİLƏMƏ (PUT) üçün eyni gövdə forması
// istifadə olunur (bax: AdminController.createProblem/updateProblem).
// Vacib sahələr (@NotBlank/@NotNull) admin panelindən yarımçıq problem
// yaradılmasının qarşısını alır.
public class ProblemUpsertRequest {

    // Problemin aid olacağı mövzunun slug-u — boş ola bilməz.
    @NotBlank
    private final String topicSlug;

    // Mövzu daxilində göstərilmə sırası — mütləq göndərilməlidir.
    @NotNull
    private final Integer orderIndex;

    // Problemin aid olduğu alt-qrup etiketi (istəyə bağlı).
    private final String subgroupLabel;

    // Problemin başlığı — boş ola bilməz.
    @NotBlank
    private final String title;

    // Problemin çətinlik səviyyəsi — mütləq göndərilməlidir.
    @NotNull
    private final Difficulty difficulty;

    // Problemin mövzu etiketləri.
    private final List<String> tags;

    // Problemin tam mətni — boş ola bilməz.
    @NotBlank
    private final String statement;

    // Giriş formatının izahı (istəyə bağlı).
    private final String inputSpec;
    // Çıxış formatının izahı (istəyə bağlı).
    private final String outputSpec;
    // Nümunə giriş (istəyə bağlı).
    private final String exampleInput;

    // Nümunə girişə uyğun gözlənilən çıxış — boş ola bilməz (JudgeService
    // müqayisə üçün buna ehtiyac duyur).
    @NotBlank
    private final String exampleOutput;

    // Həll yanaşmasının qısa izahı (istəyə bağlı).
    private final String approach;
    // Müəllim/admin üçün nümunə həll kodu (istəyə bağlı).
    private final String referenceSolution;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ProblemUpsertRequest(
        String topicSlug,
        Integer orderIndex,
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
        String referenceSolution
    ) {
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
        this.referenceSolution = referenceSolution;
    }

    // topicSlug sahəsinin dəyərini qaytarır.
    public String getTopicSlug() {
        return topicSlug;
    }

    // orderIndex sahəsinin dəyərini qaytarır.
    public Integer getOrderIndex() {
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

    // referenceSolution sahəsinin dəyərini qaytarır.
    public String getReferenceSolution() {
        return referenceSolution;
    }

    // İki ProblemUpsertRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProblemUpsertRequest that = (ProblemUpsertRequest) o;
        return Objects.equals(topicSlug, that.topicSlug)
            && Objects.equals(orderIndex, that.orderIndex)
            && Objects.equals(subgroupLabel, that.subgroupLabel)
            && Objects.equals(title, that.title)
            && difficulty == that.difficulty
            && Objects.equals(tags, that.tags)
            && Objects.equals(statement, that.statement)
            && Objects.equals(inputSpec, that.inputSpec)
            && Objects.equals(outputSpec, that.outputSpec)
            && Objects.equals(exampleInput, that.exampleInput)
            && Objects.equals(exampleOutput, that.exampleOutput)
            && Objects.equals(approach, that.approach)
            && Objects.equals(referenceSolution, that.referenceSolution);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(
            topicSlug, orderIndex, subgroupLabel, title, difficulty, tags,
            statement, inputSpec, outputSpec, exampleInput, exampleOutput, approach, referenceSolution
        );
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ProblemUpsertRequest{" +
            "topicSlug='" + topicSlug + '\'' +
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
            ", referenceSolution='" + referenceSolution + '\'' +
            '}';
    }
}
