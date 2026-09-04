package az.azcup.backend.dto.live;

import java.time.Instant;
import java.util.Objects;

// Bir canlı dərs sessiyasının hazırkı vəziyyəti — həm kod yeniləyəndə, həm
// də "GET /api/live/sessions/{code}" ilə (poll edərək) qaytarılır. Frontend
// HƏR İKİ tərəfi alır, amma yalnız QARŞI tərəfin mətnini güzgü panelinə yazır
// (öz redaktə etdiyi paneli poll cavabı ilə ÜSTÜNƏ YAZMIR — əks halda kursor
// mövqeyi/yazılan hərf itərdi).
public class LiveSessionStateDto {

    // Sessiyanı tanıdan qısa kod.
    private final String code;
    // Müəllim panelindəki hazırkı kod mətni.
    private final String teacherCode;
    // Şagird panelindəki hazırkı kod mətni.
    private final String studentCode;
    // Son dəfə hər hansı tərəfin kodu yenilədiyi vaxt.
    private final Instant lastActivity;

    // Bütün sahələri birbaşa təyin edən əsas (və yeganə) konstruktor.
    public LiveSessionStateDto(String code, String teacherCode, String studentCode, Instant lastActivity) {
        this.code = code;
        this.teacherCode = teacherCode;
        this.studentCode = studentCode;
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

    // studentCode sahəsinin dəyərini qaytarır.
    public String getStudentCode() {
        return studentCode;
    }

    // lastActivity sahəsinin dəyərini qaytarır.
    public Instant getLastActivity() {
        return lastActivity;
    }

    // İki LiveSessionStateDto obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LiveSessionStateDto that = (LiveSessionStateDto) o;
        return Objects.equals(code, that.code)
            && Objects.equals(teacherCode, that.teacherCode)
            && Objects.equals(studentCode, that.studentCode)
            && Objects.equals(lastActivity, that.lastActivity);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(code, teacherCode, studentCode, lastActivity);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "LiveSessionStateDto{" +
            "code='" + code + '\'' +
            ", teacherCode='" + teacherCode + '\'' +
            ", studentCode='" + studentCode + '\'' +
            ", lastActivity=" + lastActivity +
            '}';
    }
}
