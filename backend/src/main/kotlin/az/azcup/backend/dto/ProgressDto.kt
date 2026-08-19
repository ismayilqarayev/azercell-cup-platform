package az.azcup.backend.dto

// "GET /api/me/progress" cavabında hər mövzu üçün bir sətir — şagirdin
// hər mövzuda neçə problemi (total-dan neçəsini) həll etdiyini göstərir.
// Frontend-də irəliləyiş zolağı/faizi (məs. "7 / 10 həll edilib") üçün istifadə olunur.
data class ProgressDto(
    val topicSlug: String,
    val topicTitle: String,
    val total: Long,
    val solved: Long
)
