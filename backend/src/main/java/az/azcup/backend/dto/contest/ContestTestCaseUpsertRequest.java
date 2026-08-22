package az.azcup.backend.dto.contest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

// Bir yarış məsələsinə test halı əlavə etmək/yeniləmək üçün göndərilən gövdə.
public class ContestTestCaseUpsertRequest {

    // Test halının icra/göstərilmə sırası — mütləq göndərilməlidir.
    @NotNull
    private final Integer orderIndex;

    // Test halının girişi — boş ola bilər (bəzi məsələlərdə giriş yoxdur).
    private final String input;

    // Gözlənilən çıxış — boş ola bilməz (JudgeService müqayisə üçün buna ehtiyac duyur).
    @NotBlank
    private final String expectedOutput;

    // Bu test halının gizli olub-olmayacağı (false = şagirdə nümunə kimi göstərilir).
    @NotNull
    private final Boolean hidden;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ContestTestCaseUpsertRequest(Integer orderIndex, String input, String expectedOutput, Boolean hidden) {
        this.orderIndex = orderIndex;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.hidden = hidden;
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

    // hidden sahəsinin dəyərini qaytarır.
    public Boolean getHidden() {
        return hidden;
    }

    // İki ContestTestCaseUpsertRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContestTestCaseUpsertRequest that = (ContestTestCaseUpsertRequest) o;
        return Objects.equals(orderIndex, that.orderIndex)
            && Objects.equals(input, that.input)
            && Objects.equals(expectedOutput, that.expectedOutput)
            && Objects.equals(hidden, that.hidden);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(orderIndex, input, expectedOutput, hidden);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ContestTestCaseUpsertRequest{" +
            "orderIndex=" + orderIndex +
            ", input='" + input + '\'' +
            ", expectedOutput='" + expectedOutput + '\'' +
            ", hidden=" + hidden +
            '}';
    }
}
