package az.azcup.backend.live;

import java.time.Instant;

// Bir canlı dərs sessiyasının YADDAŞDA (verilənlər bazasında YOX) saxlanılan
// vəziyyəti — bax: LiveSessionService. Bu, bilərəkdən JPA entity DEYİL:
// sessiya keçicidir (dərs bitəndə əhəmiyyətini itirir), daimi saxlanmasına
// ehtiyac yoxdur, ona görə DB cədvəli/sxem miqrasiyası kimi əlavə
// mürəkkəblik yaratmamaq üçün sadə bir Java obyekti kimi RAM-da saxlanılır.
public class LiveSessionState {

    // Sessiyanı tanıdan qısa kod (müəllim şagirdə söz ilə deyir).
    private final String code;

    // Müəllim panelindəki hazırkı kod mətni.
    private String teacherCode = "";

    // Şagird panelindəki hazırkı kod mətni.
    private String studentCode = "";

    // Son dəfə hər hansı tərəfin kodu yenilədiyi vaxt — köhnəlmiş
    // sessiyaları avtomatik təmizləmək üçün istifadə olunur (bax:
    // LiveSessionService.cleanupExpiredSessions).
    private Instant lastActivity;

    // code sahəsini təyin edir, lastActivity-ni indiki vaxta bərabərləşdirir.
    public LiveSessionState(String code) {
        this.code = code;
        this.lastActivity = Instant.now();
    }

    // lastActivity-ni indiki vaxta yeniləyir — hər hansı tərəf kod
    // göndərəndə çağırılır ki, sessiya "aktiv" sayılsın.
    public void touch() {
        this.lastActivity = Instant.now();
    }

    // code sahəsinin dəyərini qaytarır.
    public String getCode() {
        return code;
    }

    // teacherCode sahəsinin dəyərini qaytarır.
    public String getTeacherCode() {
        return teacherCode;
    }

    // teacherCode sahəsinə yeni dəyər təyin edir.
    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    // studentCode sahəsinin dəyərini qaytarır.
    public String getStudentCode() {
        return studentCode;
    }

    // studentCode sahəsinə yeni dəyər təyin edir.
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    // lastActivity sahəsinin dəyərini qaytarır.
    public Instant getLastActivity() {
        return lastActivity;
    }
}
