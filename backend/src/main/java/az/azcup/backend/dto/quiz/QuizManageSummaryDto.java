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
    Instant updatedAt,
    // Layihə ilə gələn hazır test: silinə bilməz, yalnız dərcdən çıxarılır.
    boolean seeded,
    // Sorğu edən bu testi redaktə edə bilərmi (sahibi və ya admin). Başqasının hazır testində müəllim yalnız nəticələrə baxa və yoxlaya bilər.
    boolean canEdit
) {
}
