package az.azcup.backend.dto

data class ProgressDto(
    val topicSlug: String,
    val topicTitle: String,
    val total: Long,
    val solved: Long
)
