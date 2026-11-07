package az.azcup.backend.dto.quiz;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

// Test yaratmaq (POST) və bütöv şəkildə yeniləmək (PUT) üçün gövdə: meta məlumat + suallar siyahısı.
// Yeniləmədə suallar siyahısı testin YENİ tam halıdır: id-si olan sual yerində yenilənir, id-siz olan yeni əlavə olunur,
// siyahıda olmayan köhnə sual silinir (bu, test artıq həll olunubsa qadağandır — bax: QuizAdminService).
public record QuizUpsertRequest(
    @NotBlank @Size(max = 200) String title,
    @Size(max = 4000) String description,
    @Size(max = 80) String category,
    @Min(1) @Max(300) Integer timeLimitMinutes,
    boolean published,
    @NotNull @Size(max = 200) List<@Valid QuestionUpsert> questions
) {
    // Bir sualın yazılması. Növ və cavab sahələrinin uyğunluğu QuizAdminService-də yoxlanılır.
    public record QuestionUpsert(
        Long id,
        @NotBlank String type,
        @NotBlank @Size(max = 4000) String prompt,
        @Size(max = 8000) String code,
        List<@Size(max = 500) String> options,
        Integer correctIndex,
        @Size(max = 200) String correctText,
        @Size(max = 4000) String explanation
    ) {
    }
}
