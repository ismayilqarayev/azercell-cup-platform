package az.azcup.backend.dto.teacher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// "POST /api/teacher/groups/{groupId}/assignments/{assignmentId}/examples" gövdəsi.
public class ExampleUpsertRequest {

    @NotBlank
    @Size(max = 200)
    private final String title;

    @NotBlank
    @Size(max = 10000)
    private final String sourceCode;

    public ExampleUpsertRequest(String title, String sourceCode) {
        this.title = title;
        this.sourceCode = sourceCode;
    }

    public String getTitle() {
        return title;
    }

    public String getSourceCode() {
        return sourceCode;
    }
}
