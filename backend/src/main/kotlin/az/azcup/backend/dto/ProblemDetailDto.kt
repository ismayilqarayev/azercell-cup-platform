package az.azcup.backend.dto

import az.azcup.backend.entity.Difficulty

// Tək bir problemin TAM detalları — "GET /api/problems/{id}" cavabı.
// Problem entity-sinin birbaşa özü qaytarılmır (bax: ProblemService) ki,
// internal sahələr (məs. referenceSolution — şagirdə göstərilməməli olan
// müəllim həlli) təsadüfən API cavabına sızmasın.
data class ProblemDetailDto(
    val id: Long?,
    val topicSlug: String,
    val orderIndex: Int,
    val subgroupLabel: String?,
    val title: String,
    val difficulty: Difficulty?,
    val tags: List<String>,
    val statement: String,
    val inputSpec: String?,
    val outputSpec: String?,
    val exampleInput: String,
    val exampleOutput: String,
    val approach: String?,
    // Sorğunu edən istifadəçinin bu problemi artıq həll edib-etmədiyi —
    // hər istifadəçi üçün fərqli ola biləcəyindən Problem entity-sində
    // yox, məhz bu DTO-da hesablanaraq doldurulur.
    val solved: Boolean
)
