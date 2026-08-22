package az.azcup.backend.dto.contest;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

// Şagirdin bir yarış məsələsinə kod göndərməsi üçün gövdə —
// dto.SubmissionRequest ilə eyni forma, ayrıca saxlanılıb ki, yarış
// axını practice axınından tam müstəqil qala bilsin.
public class ContestSubmissionRequest {

    // Şagirdin göndərdiyi C++ mənbə kodu — boş ola bilməz.
    @NotBlank
    private final String sourceCode;

    // Yeganə sahəni birbaşa təyin edən konstruktor.
    public ContestSubmissionRequest(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    // sourceCode sahəsinin dəyərini qaytarır.
    public String getSourceCode() {
        return sourceCode;
    }

    // İki ContestSubmissionRequest obyektinin məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContestSubmissionRequest that = (ContestSubmissionRequest) o;
        return Objects.equals(sourceCode, that.sourceCode);
    }

    // equals() ilə uyğun hash kodu qaytarır.
    @Override
    public int hashCode() {
        return Objects.hash(sourceCode);
    }

    // Debug/log məqsədləri üçün obyektin mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ContestSubmissionRequest{sourceCode='" + sourceCode + "'}";
    }
}
