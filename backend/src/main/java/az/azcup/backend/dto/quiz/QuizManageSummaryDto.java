package az.azcup.backend.dto.quiz;

import java.time.Instant;

// Müəllim/admin idarəetmə siyahısındakı bir test.
public record QuizManageSummaryDto(
    Long id,
    String title,
    String category,
    boolean published,
    Integer timeLimitMinutes,
    int questionCount,
    // Təhvil verilmiş cəhdlərin sayı (bütün istifadəçilər üzrə).
    long attemptCount,
    String authorName,
    Instant updatedAt
) {
}
