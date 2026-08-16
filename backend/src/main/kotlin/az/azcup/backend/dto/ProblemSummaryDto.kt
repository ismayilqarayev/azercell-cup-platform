package az.azcup.backend.dto

import az.azcup.backend.entity.Difficulty

data class ProblemSummaryDto(
    val id: Long?,
    val orderIndex: Int,
    val subgroupLabel: String?,
    val title: String,
    val difficulty: Difficulty?,
    val tags: List<String>,
    val solved: Boolean
)
