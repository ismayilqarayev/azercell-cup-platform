package az.azcup.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

// "POST /api/problems/{id}/submissions" gövdəsi — şagirdin göndərdiyi
// C++ kodu. @NotBlank sayəsində boş/yalnız-boşluqlu kod Spring tərəfindən
// controller-ə çatmazdan əvvəl avtomatik rədd edilir (bax: GlobalExceptionHandler.handleValidation).
public class SubmissionRequest {

    // Şagirdin göndərdiyi C++ mənbə kodu — boş ola bilməz.
    @NotBlank
    private final String sourceCode;

    // sourceCode sahəsini birbaşa təyin edən əsas (və yeganə) konstruktor.
    public SubmissionRequest(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    // sourceCode sahəsinin dəyərini qaytarır.
    public String getSourceCode() {
        return sourceCode;
    }

    // İki SubmissionRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
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

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(sourceCode);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "SubmissionRequest{" +
            "sourceCode='" + sourceCode + '\'' +
            '}';
    }
}
