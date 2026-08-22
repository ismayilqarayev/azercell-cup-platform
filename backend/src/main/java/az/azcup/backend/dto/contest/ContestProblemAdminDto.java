package az.azcup.backend.dto.contest;

import java.util.List;
import java.util.Objects;

// Bir yarış məsələsinin MÜƏLLİM/ADMİN panelinə göstərilən forması —
// ContestProblemDto-dan fərqli olaraq, BÜTÜN test hallarını (gizli olanlar
// daxil) ehtiva edir, çünki idarəetmə panelində müəllim onları görüb
// redaktə edə bilməlidir.
public class ContestProblemAdminDto {

    // Məsələnin verilənlər bazasındakı ID-si.
    private final Long id;
    // Yarış daxilində göstərilmə sırası.
    private final int orderIndex;
    // Məsələnin başlığı.
    private final String title;
    // Məsələnin tam mətni.
    private final String statement;
    // Giriş formatının izahı.
    private final String inputSpec;
    // Çıxış formatının izahı.
    private final String outputSpec;
    // Bu məsələnin düzgün həllinə görə veriləcək bal.
    private final int points;
    // BÜTÜN test halları (gizli + nümunə).
    private final List<ContestTestCaseDto> testCases;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ContestProblemAdminDto(
        Long id,
        int orderIndex,
        String title,
        String statement,
        String inputSpec,
        String outputSpec,
        int points,
        List<ContestTestCaseDto> testCases
    ) {
        this.id = id;
        this.orderIndex = orderIndex;
        this.title = title;
        this.statement = statement;
        this.inputSpec = inputSpec;
        this.outputSpec = outputSpec;
        this.points = points;
        this.testCases = testCases;
    }

    // id sahəsinin dəyərini qaytarır.
    public Long getId() {
        return id;
    }

    // orderIndex sahəsinin dəyərini qaytarır.
    public int getOrderIndex() {
        return orderIndex;
    }

    // title sahəsinin dəyərini qaytarır.
    public String getTitle() {
        return title;
    }

    // statement sahəsinin dəyərini qaytarır.
    public String getStatement() {
        return statement;
    }

    // inputSpec sahəsinin dəyərini qaytarır.
    public String getInputSpec() {
        return inputSpec;
    }

    // outputSpec sahəsinin dəyərini qaytarır.
    public String getOutputSpec() {
        return outputSpec;
    }

    // points sahəsinin dəyərini qaytarır.
    public int getPoints() {
        return points;
    }

    // testCases sahəsinin dəyərini qaytarır.
    public List<ContestTestCaseDto> getTestCases() {
        return testCases;
    }

    // İki ContestProblemAdminDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContestProblemAdminDto that = (ContestProblemAdminDto) o;
        return orderIndex == that.orderIndex
            && points == that.points
            && Objects.equals(id, that.id)
            && Objects.equals(title, that.title)
            && Objects.equals(statement, that.statement)
            && Objects.equals(inputSpec, that.inputSpec)
            && Objects.equals(outputSpec, that.outputSpec)
            && Objects.equals(testCases, that.testCases);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(id, orderIndex, title, statement, inputSpec, outputSpec, points, testCases);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ContestProblemAdminDto{" +
            "id=" + id +
            ", orderIndex=" + orderIndex +
            ", title='" + title + '\'' +
            ", statement='" + statement + '\'' +
            ", inputSpec='" + inputSpec + '\'' +
            ", outputSpec='" + outputSpec + '\'' +
            ", points=" + points +
            ", testCases=" + testCases +
            '}';
    }
}
