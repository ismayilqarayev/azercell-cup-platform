package az.azcup.backend.dto.admin

// Müəllim panelindəki mövzu dərc/gizlətmə açar-düyməsi (toggle) üçün
// istifadə olunan minimal gövdə.
data class PublishUpdateRequest(
    val published: Boolean
)
