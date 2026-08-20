package az.azcup.backend.dto.admin;

import az.azcup.backend.entity.Difficulty;

import java.util.List;
import java.util.Objects;

// Admin panelinin problem CRUD ekranında istifadə olunan tam forma —
// adi ProblemDetailDto-dan fərqli olaraq, referenceSolution (müəllim
// həlli) DAXİLDİR, çünki bu, yalnız ADMIN roluna açıq endpoint-dir
// (bax: SecurityConfig-dəki "/api/admin/**" qaydası).
public class AdminProblemDto {

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
    private final String referenceSolution;

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

    @Override
    public int hashCode() {
        return Objects.hash(
            id, topicSlug, orderIndex, subgroupLabel, title, difficulty, tags,
            statement, inputSpec, outputSpec, exampleInput, exampleOutput, approach, referenceSolution
        );
    }

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
