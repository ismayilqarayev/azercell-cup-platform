package az.azcup.backend.dto.teacher;

// Müəllimin gördüyü kod nümunəsi; completedCount — qrupda neçə şagird onu tamamlayıb.
public class ExampleDto {

    private final Long id;
    private final String title;
    private final String sourceCode;
    private final long completedCount;

    public ExampleDto(Long id, String title, String sourceCode, long completedCount) {
        this.id = id;
        this.title = title;
        this.sourceCode = sourceCode;
        this.completedCount = completedCount;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public long getCompletedCount() {
        return completedCount;
    }
}
