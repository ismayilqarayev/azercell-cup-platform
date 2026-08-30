package az.azcup.backend.dto.teacher;

import java.util.Objects;

// Sinif jurnalındakı (gradebook) tək bir sətir — bir şagirdin bir tapşırıq
// üzrə irəliləyişi. Konkret hansı problemlərin həll edildiyi burada YOXDUR
// (bax: AssignmentDto — tapşırıq bütövlükdə mövzuya aiddir), yalnız
// say + gecikmə statusu göstərilir.
public class GradebookRowDto {

    // Şagirdin verilənlər bazasındakı ID-si.
    private final Long studentId;
    // Şagirdin tam adı.
    private final String fullName;
    // Şagirdin e-poçtu.
    private final String email;
    // Mövzudakı problemlərdən neçəsini bu şagirdin həll etdiyi.
    private final long solvedCount;
    // Mövzudakı ümumi problem sayı.
    private final long totalCount;
    // Son tarix keçib, amma bu şagird hələ bitirməyibsə true.
    private final boolean overdue;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public GradebookRowDto(Long studentId, String fullName, String email, long solvedCount, long totalCount, boolean overdue) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.solvedCount = solvedCount;
        this.totalCount = totalCount;
        this.overdue = overdue;
    }

    // studentId sahəsinin dəyərini qaytarır.
    public Long getStudentId() {
        return studentId;
    }

    // fullName sahəsinin dəyərini qaytarır.
    public String getFullName() {
        return fullName;
    }

    // email sahəsinin dəyərini qaytarır.
    public String getEmail() {
        return email;
    }

    // solvedCount sahəsinin dəyərini qaytarır.
    public long getSolvedCount() {
        return solvedCount;
    }

    // totalCount sahəsinin dəyərini qaytarır.
    public long getTotalCount() {
        return totalCount;
    }

    // overdue sahəsinin dəyərini qaytarır.
    public boolean isOverdue() {
        return overdue;
    }

    // İki GradebookRowDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GradebookRowDto that = (GradebookRowDto) o;
        return solvedCount == that.solvedCount
            && totalCount == that.totalCount
            && overdue == that.overdue
            && Objects.equals(studentId, that.studentId)
            && Objects.equals(fullName, that.fullName)
            && Objects.equals(email, that.email);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(studentId, fullName, email, solvedCount, totalCount, overdue);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "GradebookRowDto{" +
            "studentId=" + studentId +
            ", fullName='" + fullName + '\'' +
            ", email='" + email + '\'' +
            ", solvedCount=" + solvedCount +
            ", totalCount=" + totalCount +
            ", overdue=" + overdue +
            '}';
    }
}
