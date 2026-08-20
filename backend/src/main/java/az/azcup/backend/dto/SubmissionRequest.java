package az.azcup.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

// "POST /api/problems/{id}/submissions" gövdəsi — şagirdin göndərdiyi
// C++ kodu. @NotBlank sayəsində boş/yalnız-boşluqlu kod Spring tərəfindən
// controller-ə çatmazdan əvvəl avtomatik rədd edilir (bax: GlobalExceptionHandler.handleValidation).
public class SubmissionRequest {

    @NotBlank
    private final String sourceCode;

    public SubmissionRequest(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubmissionRequest that = (SubmissionRequest) o;
        return Objects.equals(sourceCode, that.sourceCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceCode);
    }

    @Override
    public String toString() {
        return "SubmissionRequest{" +
            "sourceCode='" + sourceCode + '\'' +
            '}';
    }
}
