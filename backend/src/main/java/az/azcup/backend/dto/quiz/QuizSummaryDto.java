package az.azcup.backend.dto.quiz;

// Şagirdin "İnformatika testləri" siyahısındakı bir test kartı.
public record QuizSummaryDto(
    Long id,
    String title,
    String description,
    String category,
    Integer timeLimitMinutes,
    int questionCount,
    // Bu şagirdin təhvil verdiyi cəhdlərin sayı.
    int attemptCount,
    // Ən yaxşı nəticə faizlə; hələ həll etməyibsə null.
    Integer bestPercent,
    // Təhvil verilməmiş (davam edən) cəhd var.
    boolean inProgress
) {
}
