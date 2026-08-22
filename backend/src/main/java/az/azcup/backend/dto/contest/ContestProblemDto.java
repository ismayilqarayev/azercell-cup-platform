package az.azcup.backend.dto.contest;

import java.util.List;
import java.util.Objects;

// Bir yarış məsələsinin ŞAGİRDƏ göstərilən forması. sampleTestCases
// yalnız hidden=false olan test hallarını ehtiva edir — GİZLİ testlər bu
// DTO-ya HEÇ VAXT daxil edilmir (bax: ContestService.toStudentDto), ona
// görə hansı sahə seçilib göndərilməli olduğuna diqqət yetirilməlidir.
public class ContestProblemDto {

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
    // Yalnız NÜMUNƏ (gizli olmayan) test halları.
    private final List<ContestTestCaseDto> sampleTestCases;
    // Sorğunu edən şagirdin bu məsələni artıq (tam) həll edib-etmədiyi.
    private final boolean solved;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ContestProblemDto(
        Long id,
        int orderIndex,
        String title,
        String statement,
        String inputSpec,
        String outputSpec,
        int points,
        List<ContestTestCaseDto> sampleTestCases,
        boolean solved
    ) {
        this.id = id;
        this.orderIndex = orderIndex;
        this.title = title;
        this.statement = statement;
        this.inputSpec = inputSpec;
        this.outputSpec = outputSpec;
        this.points = points;
        this.sampleTestCases = sampleTestCases;
        this.solved = solved;
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

    // sampleTestCases sahəsinin dəyərini qaytarır.
    public List<ContestTestCaseDto> getSampleTestCases() {
        return sampleTestCases;
    }

    // solved sahəsinin dəyərini qaytarır. Boolean sahə üçün getter "get"
    // əvəzinə "is" prefiksi ilə adlandırılıb (JavaBeans konvensiyası).
    public boolean isSolved() {
        return solved;
    }

    // İki ContestProblemDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContestProblemDto that = (ContestProblemDto) o;
        return orderIndex == that.orderIndex
            && points == that.points
            && solved == that.solved
            && Objects.equals(id, that.id)
            && Objects.equals(title, that.title)
            && Objects.equals(statement, that.statement)
            && Objects.equals(inputSpec, that.inputSpec)
            && Objects.equals(outputSpec, that.outputSpec)
            && Objects.equals(sampleTestCases, that.sampleTestCases);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(id, orderIndex, title, statement, inputSpec, outputSpec, points, sampleTestCases, solved);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ContestProblemDto{" +
            "id=" + id +
            ", orderIndex=" + orderIndex +
            ", title='" + title + '\'' +
            ", statement='" + statement + '\'' +
            ", inputSpec='" + inputSpec + '\'' +
            ", outputSpec='" + outputSpec + '\'' +
            ", points=" + points +
            ", sampleTestCases=" + sampleTestCases +
            ", solved=" + solved +
            '}';
    }
}
