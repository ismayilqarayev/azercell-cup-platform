package az.azcup.backend.dto.admin

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

// Admin panelində istifadəçinin ad/e-poçtunu redaktə etmək üçün
// ("PUT /api/admin/users/{id}") — parol və rol buraya daxil deyil,
// onlar üçün ayrıca endpoint-lər var (bax: RoleUpdateRequest,
// PasswordResetRequest).
data class UserProfileUpdateRequest(
    @field:NotBlank val fullName: String,
    @field:NotBlank @field:Email val email: String
)
