package az.azcup.backend.dto.admin

import az.azcup.backend.entity.Difficulty

data class AdminProblemDto(
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
    val referenceSolution: String?
)
