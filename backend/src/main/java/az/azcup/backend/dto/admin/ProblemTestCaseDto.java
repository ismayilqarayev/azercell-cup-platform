package az.azcup.backend.dto.admin;

import java.util.Objects;

// Bir əlavə (gizli) test halının admin panelinə göstərilən forması —
// yalnız TEACHER/ADMIN görür (bax: AdminController), şagirdə heç vaxt
// bu DTO qaytarılmır.
public class ProblemTestCaseDto {

    // Test halının verilənlər bazasındakı ID-si.
    private final Long id;
    // İcra sırası.
    private final int orderIndex;
    // Test halının girişi.
    private final String input;
    // Gözlənilən çıxış.
    private final String expectedOutput;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ProblemTestCaseDto(Long id, int orderIndex, String input, String expectedOutput) {
        this.id = id;
        this.orderIndex = orderIndex;
        this.input = input;
        this.expectedOutput = expectedOutput;
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
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

    // İki ProblemTestCaseDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProblemTestCaseDto that = (ProblemTestCaseDto) o;
        return orderIndex == that.orderIndex
            && Objects.equals(id, that.id)
            && Objects.equals(input, that.input)
            && Objects.equals(expectedOutput, that.expectedOutput);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(id, orderIndex, input, expectedOutput);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ProblemTestCaseDto{" +
            "id=" + id +
            ", orderIndex=" + orderIndex +
            ", input='" + input + '\'' +
            ", expectedOutput='" + expectedOutput + '\'' +
            '}';
    }
}
