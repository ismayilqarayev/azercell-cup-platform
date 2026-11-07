package az.azcup.backend.dto.quiz;

import java.util.List;

// Təhvildən sonrakı nəticə səhifəsində bir sual: şagirdin cavabı, doğru-yanlış, düzgün cavab və izah.
public record QuizReviewItemDto(
    Long questionId,
    int position,
    String type,
    String prompt,
    String code,
    List<String> options,
    // Şagirdin cavabı (cavab verməyibsə null).
    String yourAnswer,
    boolean correct,
    // CHOICE üçün düzgün variantın indeksi.
    Integer correctIndex,
    // NUMERIC üçün düzgün cavab(lar), "|" ilə ayrılmış.
    String correctText,
    String explanation
) {
}
