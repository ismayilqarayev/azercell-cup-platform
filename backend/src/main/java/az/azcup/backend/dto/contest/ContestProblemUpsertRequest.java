package az.azcup.backend.dto.contest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

// Yarış məsələsi yaratmaq/yeniləmək üçün göndərilən gövdə (test halları
// AYRICA endpoint-lə əlavə olunur — bax: ContestTestCaseUpsertRequest —
// çünki bir məsələyə çoxlu test halı ARDICIL, ayrı-ayrı əlavə edilə bilər).
public class ContestProblemUpsertRequest {

    // Yarış daxilində göstərilmə sırası — mütləq göndərilməlidir.
    @NotNull
    private final Integer orderIndex;

    // Məsələnin başlığı — boş ola bilməz.
    @NotBlank
    private final String title;

    // Məsələnin tam mətni — boş ola bilməz.
    @NotBlank
    private final String statement;

    // Giriş formatının izahı (istəyə bağlı).
    private final String inputSpec;

    // Çıxış formatının izahı (istəyə bağlı).
    private final String outputSpec;

    // Bu məsələnin düzgün həllinə görə veriləcək bal — mütləq göndərilməlidir.
    @NotNull
    private final Integer points;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public ContestProblemUpsertRequest(
        Integer orderIndex,
        String title,
        String statement,
        String inputSpec,
        String outputSpec,
        Integer points
    ) {
        this.orderIndex = orderIndex;
        this.title = title;
        this.statement = statement;
        this.inputSpec = inputSpec;
        this.outputSpec = outputSpec;
        this.points = points;
    }

    // orderIndex sahəsinin dəyərini qaytarır.
    public Integer getOrderIndex() {
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
    public Integer getPoints() {
        return points;
    }

    // İki ContestProblemUpsertRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContestProblemUpsertRequest that = (ContestProblemUpsertRequest) o;
        return Objects.equals(orderIndex, that.orderIndex)
            && Objects.equals(title, that.title)
            && Objects.equals(statement, that.statement)
            && Objects.equals(inputSpec, that.inputSpec)
            && Objects.equals(outputSpec, that.outputSpec)
            && Objects.equals(points, that.points);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(orderIndex, title, statement, inputSpec, outputSpec, points);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "ContestProblemUpsertRequest{" +
            "orderIndex=" + orderIndex +
            ", title='" + title + '\'' +
            ", statement='" + statement + '\'' +
            ", inputSpec='" + inputSpec + '\'' +
            ", outputSpec='" + outputSpec + '\'' +
            ", points=" + points +
            '}';
    }
}
