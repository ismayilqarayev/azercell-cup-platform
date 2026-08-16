package az.azcup.backend.dto.admin

data class AdminTopicDto(
    val id: Long?,
    val slug: String,
    val orderIndex: Int,
    val title: String,
    val monthTag: String?,
    val description: String?,
    val published: Boolean
)
