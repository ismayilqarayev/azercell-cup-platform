package az.azcup.backend.dto.live;

import java.time.Instant;
import java.util.List;

// Bir canlı dərs sessiyasının hazırkı vəziyyəti — həm kod yeniləyəndə, həm
// də "GET /api/live/sessions/{code}" ilə (poll edərək) qaytarılır.
//
// Müəllim tərəfi: teacherCode ÖZ paneli, students SİYAHISI hər şagirdin
// ayrı-ayrı kodunu göstərir (bax: LiveStudentCodeDto) — beləliklə eyni anda
// bir neçə şagird yaza bilir, müəllim onların HAMISINI görə bilir.
// Şagird tərəfi: yalnız teacherCode-a baxır (müəllimin panelini güzgüləyir),
// students siyahısındakı BAŞQA şagirdlərin koduna əhəmiyyət vermir.
public class LiveSessionStateDto {

    private final String code;
    private final String teacherCode;
    private final List<LiveStudentCodeDto> students;
    private final Instant lastActivity;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public LiveSessionStateDto(String code, String teacherCode, List<LiveStudentCodeDto> students, Instant lastActivity) {
        this.code = code;
        this.teacherCode = teacherCode;
        this.students = students;
        this.lastActivity = lastActivity;
    }

    // code sahəsinin dəyərini qaytarır.
    public String getCode() {
        return code;
    }

    // teacherCode sahəsinin dəyərini qaytarır.
    public String getTeacherCode() {
        return teacherCode;
    }

    // students sahəsinin dəyərini qaytarır.
    public List<LiveStudentCodeDto> getStudents() {
        return students;
    }

    // lastActivity sahəsinin dəyərini qaytarır.
    public Instant getLastActivity() {
        return lastActivity;
    }
}
