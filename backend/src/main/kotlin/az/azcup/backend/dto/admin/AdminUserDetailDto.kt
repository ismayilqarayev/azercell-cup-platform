package az.azcup.backend.dto.admin

import az.azcup.backend.entity.Role
import java.time.Instant

// Admin panelinin istifadəçi idarəetmə ekranında (tək istifadəçinin tam
// görünüşü, rol dəyişmə, aktiv/deaktiv etmə) istifadə olunan tam forma.
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
