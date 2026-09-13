package az.azcup.backend.dto.live;

// Canlı dərs sessiyasına qoşulmuş BİR şagirdin hazırkı vəziyyəti — müəllimin
// görəcəyi şagird siyahısının (bax: LiveSessionStateDto.students) hər bir elementi.
public class LiveStudentCodeDto {

    // Şagirdin istifadəçi ID-si (app_user.id) — siyahıda kimin kim olduğunu
    // ayırd etmək və seçilmiş şagirdi izləmək üçün frontend-də açar kimi istifadə olunur.
    private final Long studentId;

    // Şagirdin tam adı — siyahıda göstərmək üçün.
    private final String studentName;

    // Şagirdin öz panelindəki hazırkı kod mətni.
    private final String sourceCode;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public LiveStudentCodeDto(Long studentId, String studentName, String sourceCode) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.sourceCode = sourceCode;
    }

    // studentId sahəsinin dəyərini qaytarır.
    public Long getStudentId() {
        return studentId;
    }

    // studentName sahəsinin dəyərini qaytarır.
    public String getStudentName() {
        return studentName;
    }

    // sourceCode sahəsinin dəyərini qaytarır.
    public String getSourceCode() {
        return sourceCode;
    }
}
