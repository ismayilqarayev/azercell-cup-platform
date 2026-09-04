package az.azcup.backend.dto.live;

import java.util.Objects;

// "PUT /api/live/sessions/{code}/teacher" və ".../student" gövdəsi.
// sourceCode BOŞ ola bilər (şagird/müəllim redaktoru tamamilə silə bilər) —
// ona görə @NotBlank YOXDUR, sadəcə null göndərilirsə boş sətrə çevrilir
// (bax: LiveSessionService).
public class LiveCodeUpdateRequest {

    // Hazırkı redaktor məzmunu.
    private final String sourceCode;

    // sourceCode sahəsini birbaşa təyin edən əsas (və yeganə) konstruktor.
    public LiveCodeUpdateRequest(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    // sourceCode sahəsinin dəyərini qaytarır.
    public String getSourceCode() {
        return sourceCode;
    }

    // İki LiveCodeUpdateRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiveCodeUpdateRequest that = (LiveCodeUpdateRequest) o;
        return Objects.equals(sourceCode, that.sourceCode);
    }

    // equals() ilə uyğun hash kodu yaradır.
    @Override
    public int hashCode() {
        return Objects.hash(sourceCode);
    }

    // Debug/log məqsədləri üçün obyektin mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "LiveCodeUpdateRequest{sourceCode='" + sourceCode + "'}";
    }
}
