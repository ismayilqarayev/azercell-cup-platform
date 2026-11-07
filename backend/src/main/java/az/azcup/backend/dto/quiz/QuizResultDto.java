package az.azcup.backend.dto.quiz;

import java.time.Instant;
import java.util.List;

// Təhvil verilmiş cəhdin nəticəsi (həm təhvildən dərhal sonra, həm sonradan açıb baxanda).
public record QuizResultDto(
    Long attemptId,
    Long quizId,
    String title,
    int correctCount,
    int totalCount,
    int percent,
    // Vaxt məhdudiyyəti keçəndən sonra təhvil verilib.
    boolean late,
    Instant startedAt,
    Instant submittedAt,
    List<QuizReviewItemDto> items
) {
}
