package az.azcup.backend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

// Mövzu yaratma/yeniləmə üçün ortaq gövdə (bax: ProblemUpsertRequest-dəki
// eyni "upsert" nümunəsi).
public class TopicUpsertRequest {

    @NotBlank
    private final String slug;

    @NotNull
    private final Integer orderIndex;

    @NotBlank
    private final String title;

    private final String monthTag;
    private final String description;
    private final boolean published;

    public TopicUpsertRequest(
        String slug,
        Integer orderIndex,
        String title,
        String monthTag,
        String description,
        boolean published
    ) {
        this.slug = slug;
        this.orderIndex = orderIndex;
        this.title = title;
        this.monthTag = monthTag;
        this.description = description;
        this.published = published;
    }

    public String getSlug() {
        return slug;
    }

    public Integer getOrderIndex() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TopicUpsertRequest that = (TopicUpsertRequest) o;
        return published == that.published
            && Objects.equals(slug, that.slug)
            && Objects.equals(orderIndex, that.orderIndex)
            && Objects.equals(title, that.title)
            && Objects.equals(monthTag, that.monthTag)
            && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(slug, orderIndex, title, monthTag, description, published);
    }

    @Override
    public String toString() {
        return "TopicUpsertRequest{" +
            "slug='" + slug + '\'' +
            ", orderIndex=" + orderIndex +
            ", title='" + title + '\'' +
            ", monthTag='" + monthTag + '\'' +
            ", description='" + description + '\'' +
            ", published=" + published +
            '}';
    }
}
