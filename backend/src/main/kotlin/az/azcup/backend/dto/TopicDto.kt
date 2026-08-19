package az.azcup.backend.dto

// "GET /api/topics" cavabında hər mövzu üçün — həm ümumi məlumat (title,
// açıqlama), həm də sorğunu edən istifadəçiyə görə hesablanmış irəliləyiş
// (problemCount/solvedCount) birlikdə göstərilir.
data class TopicDto(
    val id: Long?,
    val slug: String,
    val orderIndex: Int,
    val title: String,
    val monthTag: String?,
    val description: String?,
    // Müəllim mövzunu hələ "dərc etməyibsə" false olur — bax: Topic.published.
    val published: Boolean,
    val problemCount: Long,
    val solvedCount: Long
)
