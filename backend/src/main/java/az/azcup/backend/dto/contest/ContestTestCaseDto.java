package az.azcup.backend.dto.contest;

import java.util.Objects;

// Bir test halının API-ya göstərilən forması — HƏM şagirdə (yalnız
// hidden=false olanlar, nümunə kimi), HƏM DƏ müəllim/admin panelinə
// (bax: ContestProblemAdminDto — hamısı, hidden dəyəri ilə birlikdə)
// eyni tip vasitəsilə göstərilir, çünki sahələr eynidir.
public class ContestTestCaseDto {

    // Test halının verilənlər bazasındakı ID-si.
    private final Long id;
    // Göstərilmə/icra sırası.
    private final int orderIndex;
    // Test halının girişi.
    private final String input;
    // Gözlənilən çıxış.
    private final String expectedOutput;
    // Bu test halının gizli olub-olmadığı (yalnız admin görünüşündə mənalıdır).
    private final boolean hidden;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ContestTestCaseDto(Long id, int orderIndex, String input, String expectedOutput, boolean hidden) {
        this.id = id;
        this.orderIndex = orderIndex;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.hidden = hidden;
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

    // hidden sahəsinin dəyərini qaytarır.
    public boolean isHidden() {
        return hidden;
    }

    // İki ContestTestCaseDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContestTestCaseDto that = (ContestTestCaseDto) o;
        return orderIndex == that.orderIndex
            && hidden == that.hidden
            && Objects.equals(id, that.id)
            && Objects.equals(input, that.input)
            && Objects.equals(expectedOutput, that.expectedOutput);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(id, orderIndex, input, expectedOutput, hidden);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ContestTestCaseDto{" +
            "id=" + id +
            ", orderIndex=" + orderIndex +
            ", input='" + input + '\'' +
            ", expectedOutput='" + expectedOutput + '\'' +
            ", hidden=" + hidden +
            '}';
    }
}
