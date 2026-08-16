package az.azcup.backend.dto.auth

import az.azcup.backend.entity.Role

data class AuthResponse(
    val token: String?,
    val userId: Long?,
    val fullName: String,
    val email: String,
    val role: Role?
)
