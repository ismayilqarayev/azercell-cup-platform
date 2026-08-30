package az.azcup.backend.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

// Bir problemə əlavə (gizli) test halı əlavə etmək/yeniləmək üçün göndərilən gövdə.
public class ProblemTestCaseUpsertRequest {

    // Test halının icra sırası — mütləq göndərilməlidir.
    @NotNull
    private final Integer orderIndex;

    // Test halının girişi — boş ola bilər (bəzi məsələlərdə giriş yoxdur).
    private final String input;

    // Gözlənilən çıxış — boş ola bilməz (JudgeService müqayisə üçün buna ehtiyac duyur).
    @NotBlank
    private final String expectedOutput;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ProblemTestCaseUpsertRequest(Integer orderIndex, String input, String expectedOutput) {
        this.orderIndex = orderIndex;
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    // orderIndex sahəsinin dəyərini qaytarır.
    public Integer getOrderIndex() {
        return orderIndex;
    }

    // input sahəsinin dəyərini qaytarır.
    public String getInput() {
        return input;
    }

    // expectedOutput sahəsinin dəyərini qaytarır.
    public String getExpectedOutput() {
        return expectedOutput;
    }

    // İki ProblemTestCaseUpsertRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProblemTestCaseUpsertRequest that = (ProblemTestCaseUpsertRequest) o;
        return Objects.equals(orderIndex, that.orderIndex)
            && Objects.equals(input, that.input)
            && Objects.equals(expectedOutput, that.expectedOutput);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(orderIndex, input, expectedOutput);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ProblemTestCaseUpsertRequest{" +
            "orderIndex=" + orderIndex +
            ", input='" + input + '\'' +
            ", expectedOutput='" + expectedOutput + '\'' +
            '}';
    }
}
