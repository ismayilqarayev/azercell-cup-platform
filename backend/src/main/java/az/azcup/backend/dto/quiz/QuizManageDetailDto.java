package az.azcup.backend.dto.quiz;

import java.util.List;

// Redaktor üçün testin tam halı (düzgün cavablar DAXİL). Yalnız müəllim/admin görür.
public record QuizManageDetailDto(
    Long id,
    String title,
    String description,
    String category,
    Integer timeLimitMinutes,
    boolean published,
    String authorName,
    long attemptCount,
    // Test artıq həll olunubsa true: suallar silinə, düzgün cavab/variantlar dəyişdirilə bilməz.
    boolean locked,
    List<Question> questions
) {
    public record Question(
        Long id,
        int position,
        String type,
        String prompt,
        String code,
        List<String> options,
        Integer correctIndex,
        String correctText,
        String explanation
    ) {
    }
}
