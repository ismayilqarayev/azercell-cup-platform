package az.azcup.backend.dto.quiz;

import java.util.List;

// Şagirdə testi həll edərkən göstərilən sual. DİQQƏT: düzgün cavab və izah burada YOXDUR — yalnız təhvildən sonra gəlir (QuizReviewItemDto).
public record QuizQuestionDto(
    Long id,
    int position,
    // "CHOICE" və ya "NUMERIC".
    String type,
    String prompt,
    String code,
    List<String> options
) {
}
