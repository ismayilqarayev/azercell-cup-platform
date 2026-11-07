package az.azcup.backend.dto.quiz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

// "POST /api/quiz-attempts/{id}/submit" gövdəsi. Cavab verilməyən suallar siyahıda olmaya bilər.
public record QuizSubmitRequest(
    @NotNull @Size(max = 300) List<@Valid AnswerItem> answers
) {
    // CHOICE üçün seçilən variantın indeksi (mətn kimi), NUMERIC üçün yazılan cavab.
    public record AnswerItem(@NotNull Long questionId, @Size(max = 200) String answer) {
    }
}
