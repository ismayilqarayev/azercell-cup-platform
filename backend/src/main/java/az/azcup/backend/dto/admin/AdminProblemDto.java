package az.azcup.backend.dto.admin;

import az.azcup.backend.entity.Difficulty;

import java.util.List;
import java.util.Objects;

// Admin panelinin problem CRUD ekranında istifadə olunan tam forma —
// adi ProblemDetailDto-dan fərqli olaraq, referenceSolution (müəllim
// həlli) DAXİLDİR, çünki bu, yalnız ADMIN roluna açıq endpoint-dir
// (bax: SecurityConfig-dəki "/api/admin/**" qaydası).
public class AdminProblemDto {

    // Verilənlər bazasındakı problem sətrinin ID-si.
    private final Long id;
    // Bu problemin aid olduğu mövzunun slug-u (URL-də istifadə olunan qısa ad).
    private final String topicSlug;
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
    // Müəllim/admin üçün nümunə həll kodu — şagirdlərə göstərilmir.
    private final String referenceSolution;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public AdminProblemDto(
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
        String referenceSolution
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
        this.referenceSolution = referenceSolution;
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

    // referenceSolution sahəsinin dəyərini qaytarır.
    public String getReferenceSolution() {
        return referenceSolution;
    }

    // İki AdminProblemDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdminProblemDto that = (AdminProblemDto) o;
        return orderIndex == that.orderIndex
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
            && Objects.equals(approach, that.approach)
            && Objects.equals(referenceSolution, that.referenceSolution);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(
            id, topicSlug, orderIndex, subgroupLabel, title, difficulty, tags,
            statement, inputSpec, outputSpec, exampleInput, exampleOutput, approach, referenceSolution
        );
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "AdminProblemDto{" +
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
            ", referenceSolution='" + referenceSolution + '\'' +
            '}';
    }
}
