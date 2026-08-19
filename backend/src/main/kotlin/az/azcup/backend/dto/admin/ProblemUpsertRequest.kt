package az.azcup.backend.dto.admin

import az.azcup.backend.entity.Difficulty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

// "Upsert" — həm YARATMA (POST), həm YENİLƏMƏ (PUT) üçün eyni gövdə forması
// istifadə olunur (bax: AdminController.createProblem/updateProblem).
// Vacib sahələr (@NotBlank/@NotNull) admin panelindən yarımçıq problem
// yaradılmasının qarşısını alır.
data class ProblemUpsertRequest(
    @field:NotBlank val topicSlug: String,
    @field:NotNull val orderIndex: Int?,
    val subgroupLabel: String?,
    @field:NotBlank val title: String,
    @field:NotNull val difficulty: Difficulty?,
    val tags: List<String>?,
    @field:NotBlank val statement: String,
    val inputSpec: String?,
    val outputSpec: String?,
    val exampleInput: String?,
    @field:NotBlank val exampleOutput: String,
    val approach: String?,
    val referenceSolution: String?
)
