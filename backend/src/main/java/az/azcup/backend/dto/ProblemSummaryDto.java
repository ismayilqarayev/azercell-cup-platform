package az.azcup.backend.dto;

import az.azcup.backend.entity.Difficulty;

import java.util.List;
import java.util.Objects;

// Bir mövzunun problem SİYAHISINDA göstərilən qısa forma (statement, io
// spesifikasiyaları və s. daxil deyil — bunlar yalnız ProblemDetailDto-da,
// konkret problem açılanda gətirilir ki, siyahı sorğusu yüngül qalsın).
public class ProblemSummaryDto {

    private final Long id;
    private final int orderIndex;
    private final String subgroupLabel;
    private final String title;
    private final Difficulty difficulty;
    private final List<String> tags;
    private final boolean solved;

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

    public Long getId() {
        return id;
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
        ProblemSummaryDto that = (ProblemSummaryDto) o;
        return orderIndex == that.orderIndex
            && solved == that.solved
            && Objects.equals(id, that.id)
            && Objects.equals(subgroupLabel, that.subgroupLabel)
            && Objects.equals(title, that.title)
            && difficulty == that.difficulty
            && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderIndex, subgroupLabel, title, difficulty, tags, solved);
    }

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
