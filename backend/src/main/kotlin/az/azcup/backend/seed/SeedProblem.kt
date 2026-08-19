package az.azcup.backend.seed

// seed-data.json-dakı bir problem sətrinin Kotlin qarşılığı.
// DİQQƏT: difficulty burada String-dir (Problem.difficulty-dəki
// Difficulty enum-u YOX) — çünki JSON mətn kimi "easy"/"mid"/"hard"
// saxlayır, SeedLoader onu Difficulty.valueOf(sp.difficulty.uppercase())
// ilə əl ilə enum-a çevirir.
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
