package az.azcup.backend.judge;

import java.util.Objects;

// JudgeService.judgeMultiple-ə ötürülən TƏK BİR test halı — ContestTestCase
// entity-sinin yoxlama üçün lazım olan minimal görünüşü. Bu tip ayrıca
// saxlanılır ki, judge paketi ContestTestCase (entity qatı) ilə birbaşa
// bağlı olmasın — JudgeService yalnız "sıra + giriş + gözlənilən çıxış"
// bilməlidir, entity-nin özünü tanımasına ehtiyac yoxdur.
public class TestCaseInput {

    // Bu test halının sırası — nəticədə "hansı test uğursuz oldu" deyəndə
    // şagirdə göstəriləcək nömrə budur.
    private final int orderIndex;
    // Test halının girişi (stdin).
    private final String input;
    // Bu girişə uyğun gözlənilən düzgün çıxış.
    private final String expectedOutput;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public TestCaseInput(int orderIndex, String input, String expectedOutput) {
        this.orderIndex = orderIndex;
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    // orderIndex sahəsinin dəyərini qaytarır.
    public int getOrderIndex() {
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

    // İki TestCaseInput obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TestCaseInput that = (TestCaseInput) o;
        return orderIndex == that.orderIndex
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
        return "TestCaseInput{" +
            "orderIndex=" + orderIndex +
            ", input='" + input + '\'' +
            ", expectedOutput='" + expectedOutput + '\'' +
            '}';
    }
}
