package az.azcup.backend.dto.admin

import az.azcup.backend.entity.Role
import java.time.Instant

data class AdminUserDetailDto(
    val id: Long?,
    val fullName: String,
    val email: String,
    val role: Role?,
    val approved: Boolean,
    val active: Boolean,
    val createdAt: Instant?,
    val totalSolved: Long
)
