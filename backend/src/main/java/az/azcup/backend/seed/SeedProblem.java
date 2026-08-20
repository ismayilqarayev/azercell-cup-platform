package az.azcup.backend.seed;

import java.util.List;
import java.util.Objects;

// seed-data.json-dakı bir problem sətrinin Java qarşılığı.
// DİQQƏT: difficulty burada String-dir (Problem.difficulty-dəki
// Difficulty enum-u YOX) — çünki JSON mətn kimi "easy"/"mid"/"hard"
// saxlayır, SeedLoader onu Difficulty.valueOf(sp.difficulty().toUpperCase())
// ilə əl ilə enum-a çevirir.
public class SeedProblem {

    private final String topicSlug;
    private final int orderIndex;
    private final String subgroupLabel;
    private final String title;
    private final String difficulty;
    private final List<String> tags;
    private final String statement;
    private final String inputSpec;
    private final String outputSpec;
    private final String exampleInput;
    private final String exampleOutput;
    private final String approach;
    private final String referenceSolution;

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

    public String getTopicSlug() {
        return topicSlug;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public String getSubgroupLabel() {
        return subgroupLabel;
    }

    public String getTitle() {
        return title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getStatement() {
        return statement;
    }

    public String getInputSpec() {
        return inputSpec;
    }

    public String getOutputSpec() {
        return outputSpec;
    }

    public String getExampleInput() {
        return exampleInput;
    }

    public String getExampleOutput() {
        return exampleOutput;
    }

    public String getApproach() {
        return approach;
    }

    public String getReferenceSolution() {
        return referenceSolution;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(
            topicSlug, orderIndex, subgroupLabel, title, difficulty, tags,
            statement, inputSpec, outputSpec, exampleInput, exampleOutput, approach, referenceSolution
        );
    }

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
