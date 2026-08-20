package az.azcup.backend.dto;

import java.util.Objects;

// "GET /api/topics" cavabında hər mövzu üçün — həm ümumi məlumat (title,
// açıqlama), həm də sorğunu edən istifadəçiyə görə hesablanmış irəliləyiş
// (problemCount/solvedCount) birlikdə göstərilir.
public class TopicDto {

    private final Long id;
    private final String slug;
    private final int orderIndex;
    private final String title;
    private final String monthTag;
    private final String description;
    // Müəllim mövzunu hələ "dərc etməyibsə" false olur — bax: Topic.published.
    private final boolean published;
    private final long problemCount;
    private final long solvedCount;

    public TopicDto(
        Long id,
        String slug,
        int orderIndex,
        String title,
        String monthTag,
        String description,
        boolean published,
        long problemCount,
        long solvedCount
    ) {
        this.id = id;
        this.slug = slug;
        this.orderIndex = orderIndex;
        this.title = title;
        this.monthTag = monthTag;
        this.description = description;
        this.published = published;
        this.problemCount = problemCount;
        this.solvedCount = solvedCount;
    }

    public Long getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public String getTitle() {
        return title;
    }

    public String getMonthTag() {
        return monthTag;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPublished() {
        return published;
    }

    public long getProblemCount() {
        return problemCount;
    }

    public long getSolvedCount() {
        return solvedCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TopicDto that = (TopicDto) o;
        return orderIndex == that.orderIndex
            && published == that.published
            && problemCount == that.problemCount
            && solvedCount == that.solvedCount
            && Objects.equals(id, that.id)
            && Objects.equals(slug, that.slug)
            && Objects.equals(title, that.title)
            && Objects.equals(monthTag, that.monthTag)
            && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, slug, orderIndex, title, monthTag, description, published, problemCount, solvedCount);
    }

    @Override
    public String toString() {
        return "TopicDto{" +
            "id=" + id +
            ", slug='" + slug + '\'' +
            ", orderIndex=" + orderIndex +
            ", title='" + title + '\'' +
            ", monthTag='" + monthTag + '\'' +
            ", description='" + description + '\'' +
            ", published=" + published +
            ", problemCount=" + problemCount +
            ", solvedCount=" + solvedCount +
            '}';
    }
}
