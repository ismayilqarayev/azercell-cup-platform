package az.azcup.backend.dto.quiz;

import java.time.Instant;
import java.util.List;

// Müəllimin nəticə cədvəli: şagirdlər üzrə ən yaxşı nəticə və sual üzrə doğru cavab faizi.
public record QuizResultsDto(
    String title,
    int questionCount,
    // "ALL" (admin: hamı) və ya "MY_GROUPS" (müəllim: yalnız öz qruplarındakı şagirdlər).
    String scope,
    List<StudentRow> students,
    List<QuestionStat> questions
) {
    public record StudentRow(
        Long studentId,
        String name,
        String email,
        int attemptCount,
        int bestCorrect,
        int bestTotal,
        int bestPercent,
        Instant lastSubmittedAt
    ) {
    }

    public record QuestionStat(
        Long questionId,
        int position,
        // Sualın qısaldılmış mətni.
        String prompt,
        int answered,
        int correct,
        int percent
    ) {
    }
}
