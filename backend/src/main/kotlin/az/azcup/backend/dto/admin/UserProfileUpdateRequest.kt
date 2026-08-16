package az.azcup.backend.dto.admin

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserProfileUpdateRequest(
    @field:NotBlank val fullName: String,
    @field:NotBlank @field:Email val email: String
)
