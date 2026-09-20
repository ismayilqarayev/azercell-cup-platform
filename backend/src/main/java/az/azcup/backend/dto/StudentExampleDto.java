package az.azcup.backend.dto;

// Şagirdə göstərilən bir kod nümunəsi (baxıb yazması üçün) və onu artıq tamamlayıb-tamamlamadığı.
public class StudentExampleDto {

    private final Long id;
    private final String title;
    private final String sourceCode;
    private final boolean completed;

    public StudentExampleDto(Long id, String title, String sourceCode, boolean completed) {
        this.id = id;
        this.title = title;
        this.sourceCode = sourceCode;
        this.completed = completed;
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

    public boolean isCompleted() {
        return completed;
    }
}
