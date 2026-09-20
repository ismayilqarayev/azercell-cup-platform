package az.azcup.backend.dto.teacher;

// Müəllimin jurnalı üçün: qrupun hər şagirdi tapşırığın kod nümunələrindən neçəsini tamamlayıb.
public class ExampleProgressRowDto {

    private final Long studentId;
    private final long done;
    private final long total;

    public ExampleProgressRowDto(Long studentId, long done, long total) {
        this.studentId = studentId;
        this.done = done;
        this.total = total;
    }

    public Long getStudentId() {
        return studentId;
    }

    public long getDone() {
        return done;
    }

    public long getTotal() {
        return total;
    }
}
