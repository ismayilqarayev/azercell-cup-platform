package az.azcup.backend.dto.admin

import java.time.Instant

// Sadə istifadəçi siyahıları üçün (məs. "GET /api/admin/users" — şagird
// siyahısı, "GET /api/admin/pending-teachers" — təsdiq gözləyən müəllimlər).
// Rol/aktivlik/təsdiq statusu kimi ətraflı sahələr yoxdur — bunlar üçün
// AdminUserDetailDto istifadə olunur (bax: "/api/admin/users/{id}").
data class AdminUserDto(
    val id: Long?,
    val fullName: String,
    val email: String,
    val createdAt: Instant?,
    val totalSolved: Long
)
