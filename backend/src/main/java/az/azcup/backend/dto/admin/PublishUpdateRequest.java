package az.azcup.backend.dto.admin;

import java.util.Objects;

// Müəllim panelindəki mövzu dərc/gizlətmə açar-düyməsi (toggle) üçün
// istifadə olunan minimal gövdə.
public class PublishUpdateRequest {

    private final boolean published;

    public PublishUpdateRequest(boolean published) {
        this.published = published;
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
        PublishUpdateRequest that = (PublishUpdateRequest) o;
        return published == that.published;
    }

    @Override
    public int hashCode() {
        return Objects.hash(published);
    }

    @Override
    public String toString() {
        return "PublishUpdateRequest{" +
            "published=" + published +
            '}';
    }
}
