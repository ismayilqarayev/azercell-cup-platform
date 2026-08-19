package az.azcup.backend.dto.admin

// Admin panelinin mövzu CRUD ekranında istifadə olunan forma —
// problemCount/solvedCount kimi hesablanmış sahələr YOXDUR (TopicDto-dan
// fərqli olaraq), çünki bu, mövzunu redaktə etmək üçündür, statistika
// göstərmək üçün yox.
data class AdminTopicDto(
    val id: Long?,
    val slug: String,
    val orderIndex: Int,
    val title: String,
    val monthTag: String?,
    val description: String?,
    val published: Boolean
)
