package az.azcup.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// "POST /api/me/assignments/{id}/examples/{exampleId}/check" gövdəsi.
public class ExampleCheckRequest {

    @NotBlank
    @Size(max = 20000)
    private final String typedCode;

    public ExampleCheckRequest(String typedCode) {
        this.typedCode = typedCode;
    }

    public String getTypedCode() {
        return typedCode;
    }
}
