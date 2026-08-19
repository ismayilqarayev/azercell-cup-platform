package az.azcup.backend.dto.admin

import az.azcup.backend.entity.Role
import jakarta.validation.constraints.NotNull

// Admin panelində bir istifadəçinin rolunu dəyişmək üçün
// ("PUT /api/admin/users/{id}/role").
data class RoleUpdateRequest(
    @field:NotNull val role: Role?
)
