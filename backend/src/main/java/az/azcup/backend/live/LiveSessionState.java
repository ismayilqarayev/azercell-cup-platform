package az.azcup.backend.live;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

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

    // Sessiyaya qoşulan HƏR şagirdin öz kodu — açar onun istifadəçi ID-sidir.
    // LinkedHashMap seçilib ki, müəllimin görəcəyi siyahıda şagirdlər QOŞULMA
    // sırası ilə qalsın (hər poll-da təsadüfi sıralanmasın).
    private final Map<Long, StudentEntry> students = new LinkedHashMap<>();

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

    // Bir şagirdin kodunu yeniləyir — həmin şagird hələ siyahıda yoxdursa
    // (sessiyaya İLK dəfə yazırsa), avtomatik əlavə olunur.
    public void setStudentCode(Long studentId, String studentName, String sourceCode) {
        StudentEntry entry = students.computeIfAbsent(studentId, id -> new StudentEntry(studentName));
        entry.setStudentName(studentName); // adı hər zaman ən son (JWT-dəki) dəyərə uyğunlaşdırır
        entry.setSourceCode(sourceCode);
    }

    // Bütün qoşulmuş şagirdlərin xəritəsini qaytarır (id -> ad+kod).
    public Map<Long, StudentEntry> getStudents() {
        return students;
    }

    // lastActivity sahəsinin dəyərini qaytarır.
    public Instant getLastActivity() {
        return lastActivity;
    }

    // Bir şagirdin adı və hazırkı kodu — students xəritəsinin dəyəri.
    public static class StudentEntry {

        private String studentName;
        private String sourceCode = "";

        public StudentEntry(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getSourceCode() {
            return sourceCode;
        }

        public void setSourceCode(String sourceCode) {
            this.sourceCode = sourceCode;
        }
    }
}
