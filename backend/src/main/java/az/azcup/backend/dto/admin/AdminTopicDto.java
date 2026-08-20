package az.azcup.backend.dto.admin;

import java.util.Objects;

// Admin panelinin mövzu CRUD ekranında istifadə olunan forma —
// problemCount/solvedCount kimi hesablanmış sahələr YOXDUR (TopicDto-dan
// fərqli olaraq), çünki bu, mövzunu redaktə etmək üçündür, statistika
// göstərmək üçün yox.
public class AdminTopicDto {

    private final Long id;
    private final String slug;
    private final int orderIndex;
    private final String title;
    private final String monthTag;
    private final String description;
    private final boolean published;

    public AdminTopicDto(
        Long id,
        String slug,
        int orderIndex,
        String title,
        String monthTag,
        String description,
        boolean published
    ) {
        this.id = id;
        this.slug = slug;
        this.orderIndex = orderIndex;
        this.title = title;
        this.monthTag = monthTag;
        this.description = description;
        this.published = published;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AdminTopicDto that = (AdminTopicDto) o;
        return orderIndex == that.orderIndex
            && published == that.published
            && Objects.equals(id, that.id)
            && Objects.equals(slug, that.slug)
            && Objects.equals(title, that.title)
            && Objects.equals(monthTag, that.monthTag)
            && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, slug, orderIndex, title, monthTag, description, published);
    }

    @Override
    public String toString() {
        return "AdminTopicDto{" +
            "id=" + id +
            ", slug='" + slug + '\'' +
            ", orderIndex=" + orderIndex +
            ", title='" + title + '\'' +
            ", monthTag='" + monthTag + '\'' +
            ", description='" + description + '\'' +
            ", published=" + published +
            '}';
    }
}
