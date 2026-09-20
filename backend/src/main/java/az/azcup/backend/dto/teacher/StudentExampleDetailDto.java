package az.azcup.backend.dto.teacher;

import java.time.Instant;

// Müəllimin bir şagirdin bir nümunə üzrə irəliləyişini görməsi üçün: tamamlayıb-tamamlamadığı və nə vaxt,
// neçə cəhd etdiyi, ən yaxşı/son uyğunluq faizi və ən son yazdığı kod (cəhd yoxdursa null-lar).
public class StudentExampleDetailDto {

    private final Long exampleId;
    private final String title;
    private final boolean completed;
    private final Instant completedAt;
    private final long attempts;
    private final int bestPercent;
    private final int lastPercent;
    private final Instant lastAttemptAt;
    private final String lastTypedCode;

    public StudentExampleDetailDto(
        Long exampleId, String title, boolean completed, Instant completedAt,
        long attempts, int bestPercent, int lastPercent, Instant lastAttemptAt, String lastTypedCode
    ) {
        this.exampleId = exampleId;
        this.title = title;
        this.completed = completed;
        this.completedAt = completedAt;
        this.attempts = attempts;
        this.bestPercent = bestPercent;
        this.lastPercent = lastPercent;
        this.lastAttemptAt = lastAttemptAt;
        this.lastTypedCode = lastTypedCode;
    }

    public Long getExampleId() {
        return exampleId;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public long getAttempts() {
        return attempts;
    }

    public int getBestPercent() {
        return bestPercent;
    }

    public int getLastPercent() {
        return lastPercent;
    }

    public Instant getLastAttemptAt() {
        return lastAttemptAt;
    }

    public String getLastTypedCode() {
        return lastTypedCode;
    }
}
