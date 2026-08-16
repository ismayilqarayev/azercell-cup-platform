package az.azcup.backend.dto.admin

import az.azcup.backend.entity.Role
import jakarta.validation.constraints.NotNull

data class RoleUpdateRequest(
    @field:NotNull val role: Role?
)
