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

    @NotBlank
    private final String topicSlug;

    @NotNull
    private final Integer orderIndex;

    private final String subgroupLabel;

    @NotBlank
    private final String title;

    @NotNull
    private final Difficulty difficulty;

    private final List<String> tags;

    @NotBlank
    private final String statement;

    private final String inputSpec;
    private final String outputSpec;
    private final String exampleInput;

    @NotBlank
    private final String exampleOutput;

    private final String approach;
    private final String referenceSolution;

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

    public String getTopicSlug() {
        return topicSlug;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public String getSubgroupLabel() {
        return subgroupLabel;
    }

    public String getTitle() {
        return title;
    }

    public Difficulty getDifficulty() {
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

    @Override
    public int hashCode() {
        return Objects.hash(
            topicSlug, orderIndex, subgroupLabel, title, difficulty, tags,
            statement, inputSpec, outputSpec, exampleInput, exampleOutput, approach, referenceSolution
        );
    }

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
