package az.azcup.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

// "POST /api/run" gövdəsi — sərbəst kod yazma sahəsindən gələn kod və
// (istəyə bağlı) stdin. SubmissionRequest-dən fərqli olaraq heç bir
// problem ID-si YOXDUR, çünki bu, konkret bir problemə bağlı deyil.
public class RunRequest {

    @NotBlank
    private final String sourceCode;

    private final String stdin;

    public RunRequest(String sourceCode, String stdin) {
        this.sourceCode = sourceCode;
        if (stdin != null) {
            this.stdin = stdin;
        } else {
            this.stdin = "";
        }
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public String getStdin() {
        return stdin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RunRequest that = (RunRequest) o;
        return Objects.equals(sourceCode, that.sourceCode) && Objects.equals(stdin, that.stdin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceCode, stdin);
    }

    @Override
    public String toString() {
        return "RunRequest{" +
            "sourceCode='" + sourceCode + '\'' +
            ", stdin='" + stdin + '\'' +
            '}';
    }
}
