package az.azcup.backend.dto.live;

import az.azcup.backend.entity.SubmissionStatus;

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

    // Şagird "✋ Kömək istə" düyməsini basıbsa true — müəllim panelində bu
    // şagirdin çipini fərqləndirmək üçün (bax: index.html renderLiveStudentList).
    private final boolean needsHelp;

    // Şagirdin son "İşə sal" nəticəsinin statusu (ACCEPTED, COMPILE_ERROR,
    // RUNTIME_ERROR, TIME_LIMIT_EXCEEDED) — hələ icra etməyibsə null.
    private final SubmissionStatus lastStatus;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public LiveStudentCodeDto(Long studentId, String studentName, String sourceCode, boolean needsHelp, SubmissionStatus lastStatus) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.sourceCode = sourceCode;
        this.needsHelp = needsHelp;
        this.lastStatus = lastStatus;
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

    // needsHelp sahəsinin dəyərini qaytarır.
    public boolean isNeedsHelp() {
        return needsHelp;
    }

    // lastStatus sahəsinin dəyərini qaytarır.
    public SubmissionStatus getLastStatus() {
        return lastStatus;
    }
}
