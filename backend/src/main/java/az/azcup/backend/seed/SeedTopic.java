package az.azcup.backend.seed;

import java.util.Objects;

// seed-data.json faylındakı bir mövzu sətrinin Java qarşılığı — Jackson
// (ObjectMapper) JSON-u birbaşa bu tipə deserializasiya edir (bax: SeedLoader).
// Topic entity-si ilə demək olar eynidir, amma ayrıca saxlanılıb ki, seed
// formatı ilə verilənlər bazası sxemi bir-birindən MÜSTƏQİL dəyişə bilsin.
public class SeedTopic {

    private final String slug;
    private final int orderIndex;
    private final String title;
    private final String monthTag;
    private final String description;
    private final boolean published;

    public SeedTopic(String slug, int orderIndex, String title, String monthTag, String description, boolean published) {
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
        SeedTopic that = (SeedTopic) o;
        return orderIndex == that.orderIndex
            && published == that.published
            && Objects.equals(slug, that.slug)
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
        return "SeedTopic{" +
            "slug='" + slug + '\'' +
            ", orderIndex=" + orderIndex +
            ", title='" + title + '\'' +
            ", monthTag='" + monthTag + '\'' +
            ", description='" + description + '\'' +
            ", published=" + published +
            '}';
    }
}
