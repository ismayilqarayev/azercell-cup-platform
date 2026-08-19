package az.azcup.backend.dto

import az.azcup.backend.entity.Difficulty

// Bir mövzunun problem SİYAHISINDA göstərilən qısa forma (statement, io
// spesifikasiyaları və s. daxil deyil — bunlar yalnız ProblemDetailDto-da,
// konkret problem açılanda gətirilir ki, siyahı sorğusu yüngül qalsın).
data class ProblemSummaryDto(
    val id: Long?,
    val orderIndex: Int,
    val subgroupLabel: String?,
    val title: String,
    val difficulty: Difficulty?,
    val tags: List<String>,
    val solved: Boolean
)
