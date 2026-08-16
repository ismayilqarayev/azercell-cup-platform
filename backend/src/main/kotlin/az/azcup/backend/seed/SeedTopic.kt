package az.azcup.backend.seed

data class SeedTopic(
    val slug: String,
    val orderIndex: Int,
    val title: String,
    val monthTag: String?,
    val description: String?,
    val published: Boolean
)
