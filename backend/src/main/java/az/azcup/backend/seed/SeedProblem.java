package az.azcup.backend.seed;

import java.util.List;
import java.util.Objects;

// seed-data.json-dakı bir problem sətrinin Java qarşılığı.
// DİQQƏT: difficulty burada String-dir (Problem.difficulty-dəki
// Difficulty enum-u YOX) — çünki JSON mətn kimi "easy"/"mid"/"hard"
// saxlayır, SeedLoader onu Difficulty.valueOf(sp.difficulty().toUpperCase())
// ilə əl ilə enum-a çevirir.
public class SeedProblem {

    // Problemin aid olduğu mövzunun slug-u.
    private final String topicSlug;
    // Mövzu daxilində göstərilmə sırası.
    private final int orderIndex;
    // Alt-qrup etiketi (ola bilər null).
    private final String subgroupLabel;
    // Problemin başlığı.
    private final String title;
    // Çətinlik səviyyəsi, JSON-da mətn kimi ("easy"/"mid"/"hard").
    private final String difficulty;
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
    // Həll yanaşmasının qısa izahı.
    private final String approach;
    // Müəllim/admin üçün nümunə həll kodu.
    private final String referenceSolution;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor —
    // Jackson JSON-dan deserializasiya edərkən bunu çağırır.
    public SeedProblem(
        String topicSlug,
        int orderIndex,
        String subgroupLabel,
        String title,
        String difficulty,
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
    public String getDifficulty() {
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

    // İki SeedProblem obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SeedProblem that = (SeedProblem) o;
        return orderIndex == that.orderIndex
            && Objects.equals(topicSlug, that.topicSlug)
            && Objects.equals(subgroupLabel, that.subgroupLabel)
            && Objects.equals(title, that.title)
            && Objects.equals(difficulty, that.difficulty)
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
        return "SeedProblem{" +
            "topicSlug='" + topicSlug + '\'' +
            ", orderIndex=" + orderIndex +
            ", subgroupLabel='" + subgroupLabel + '\'' +
            ", title='" + title + '\'' +
            ", difficulty='" + difficulty + '\'' +
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
