package az.azcup.backend.dto.admin

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

// Mövzu yaratma/yeniləmə üçün ortaq gövdə (bax: ProblemUpsertRequest-dəki
// eyni "upsert" nümunəsi).
data class TopicUpsertRequest(
    @field:NotBlank val slug: String,
    @field:NotNull val orderIndex: Int?,
    @field:NotBlank val title: String,
    val monthTag: String?,
    val description: String?,
    val published: Boolean
)
