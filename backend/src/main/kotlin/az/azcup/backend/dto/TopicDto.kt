package az.azcup.backend.dto

data class TopicDto(
    val id: Long?,
    val slug: String,
    val orderIndex: Int,
    val title: String,
    val monthTag: String?,
    val description: String?,
    val published: Boolean,
    val problemCount: Long,
    val solvedCount: Long
)
