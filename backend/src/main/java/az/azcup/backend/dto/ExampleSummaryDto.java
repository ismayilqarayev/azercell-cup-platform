package az.azcup.backend.dto;

// Şagirdin "Tapşırıqlarım" kartında göstərilən: bu tapşırıqda neçə kod nümunəsi var, neçəsini tamamlayıb.
public class ExampleSummaryDto {

    private final Long assignmentId;
    private final long total;
    private final long done;

    public ExampleSummaryDto(Long assignmentId, long total, long done) {
        this.assignmentId = assignmentId;
        this.total = total;
        this.done = done;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public long getTotal() {
        return total;
    }

    public long getDone() {
        return done;
    }
}
