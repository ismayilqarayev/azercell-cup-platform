package az.azcup.backend.seed

// seed-data.json faylındakı bir mövzu sətrinin Kotlin qarşılığı — Jackson
// (ObjectMapper) JSON-u birbaşa bu tipə deserializasiya edir (bax: SeedLoader).
// Topic entity-si ilə demək olar eynidir, amma ayrıca saxlanılıb ki, seed
// formatı ilə verilənlər bazası sxemi bir-birindən MÜSTƏQİL dəyişə bilsin.
data class SeedTopic(
    val slug: String,
    val orderIndex: Int,
    val title: String,
    val monthTag: String?,
    val description: String?,
    val published: Boolean
)
