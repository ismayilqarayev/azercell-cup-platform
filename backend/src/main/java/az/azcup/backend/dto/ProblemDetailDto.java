package az.azcup.backend.dto;

import az.azcup.backend.entity.Difficulty;

import java.util.List;
import java.util.Objects;

// Tək bir problemin TAM detalları — "GET /api/problems/{id}" cavabı.
// Problem entity-sinin birbaşa özü qaytarılmır (bax: ProblemService) ki,
// internal sahələr (məs. referenceSolution — şagirdə göstərilməməli olan
// müəllim həlli) təsadüfən API cavabına sızmasın.
public class ProblemDetailDto {

    private final Long id;
    private final String topicSlug;
    private final int orderIndex;
    private final String subgroupLabel;
    private final String title;
    private final Difficulty difficulty;
    private final List<String> tags;
    private final String statement;
    private final String inputSpec;
    private final String outputSpec;
    private final String exampleInput;
    private final String exampleOutput;
    private final String approach;
    // Sorğunu edən istifadəçinin bu problemi artıq həll edib-etmədiyi —
    // hər istifadəçi üçün fərqli ola biləcəyindən Problem entity-sində
    // yox, məhz bu DTO-da hesablanaraq doldurulur.
    private final boolean solved;

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

    public Long getId() {
        return id;
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

    public boolean isSolved() {
        return solved;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(
            id, topicSlug, orderIndex, subgroupLabel, title, difficulty, tags,
            statement, inputSpec, outputSpec, exampleInput, exampleOutput, approach, solved
        );
    }

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
