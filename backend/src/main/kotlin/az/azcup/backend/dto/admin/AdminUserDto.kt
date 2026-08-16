package az.azcup.backend.dto.admin

import java.time.Instant

data class AdminUserDto(
    val id: Long?,
    val fullName: String,
    val email: String,
    val createdAt: Instant?,
    val totalSolved: Long
)
