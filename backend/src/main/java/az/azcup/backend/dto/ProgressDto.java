package az.azcup.backend.dto;

import java.util.Objects;

// "GET /api/me/progress" cavabında hər mövzu üçün bir sətir — şagirdin
// hər mövzuda neçə problemi (total-dan neçəsini) həll etdiyini göstərir.
// Frontend-də irəliləyiş zolağı/faizi (məs. "7 / 10 həll edilib") üçün istifadə olunur.
public class ProgressDto {

    private final String topicSlug;
    private final String topicTitle;
    private final long total;
    private final long solved;

    public ProgressDto(String topicSlug, String topicTitle, long total, long solved) {
        this.topicSlug = topicSlug;
        this.topicTitle = topicTitle;
        this.total = total;
        this.solved = solved;
    }

    public String getTopicSlug() {
        return topicSlug;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public long getTotal() {
        return total;
    }

    public long getSolved() {
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
        ProgressDto that = (ProgressDto) o;
        return total == that.total
            && solved == that.solved
            && Objects.equals(topicSlug, that.topicSlug)
            && Objects.equals(topicTitle, that.topicTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topicSlug, topicTitle, total, solved);
    }

    @Override
    public String toString() {
        return "ProgressDto{" +
            "topicSlug='" + topicSlug + '\'' +
            ", topicTitle='" + topicTitle + '\'' +
            ", total=" + total +
            ", solved=" + solved +
            '}';
    }
}
