package az.azcup.backend.seed

data class SeedProblem(
    val topicSlug: String,
    val orderIndex: Int,
    val subgroupLabel: String?,
    val title: String,
    val difficulty: String,
    val tags: List<String>?,
    val statement: String,
    val inputSpec: String?,
    val outputSpec: String?,
    val exampleInput: String?,
    val exampleOutput: String,
    val approach: String?,
    val referenceSolution: String?
)
