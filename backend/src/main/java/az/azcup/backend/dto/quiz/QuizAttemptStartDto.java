package az.azcup.backend.dto.quiz;

import java.time.Instant;
import java.util.List;

// Testi başlayanda qaytarılır: cəhdin ID-si, vaxt məlumatı və (cavabsız) suallar.
public record QuizAttemptStartDto(
    Long attemptId,
    Long quizId,
    String title,
    Integer timeLimitMinutes,
    Instant startedAt,
    // Vaxt məhdudiyyəti yoxdursa null.
    Instant deadlineAt,
    // Serverin indiki saatı: brauzerin saatı səhv olsa da geri sayım düz hesablansın deyə.
    Instant serverNow,
    List<QuizQuestionDto> questions
) {
}
