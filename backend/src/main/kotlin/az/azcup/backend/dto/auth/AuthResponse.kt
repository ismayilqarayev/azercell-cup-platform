package az.azcup.backend.dto.auth

import az.azcup.backend.entity.Role

// Həm login, həm də register uğurlu olduqda qaytarılan cavab —
// frontend token-i localStorage-a yazır və sonrakı bütün sorğularda
// "Authorization: Bearer <token>" kimi göndərir.
data class AuthResponse(
    // Müəllim qeydiyyatdan keçib, hələ admin tərəfindən təsdiqlənməyibsə
    // token null ola bilər — hesab yaranıb, amma daxil olmaq mümkün deyil
    // (bax: AuthService.register — TEACHER rolunda approved=false).
    val token: String?,
    val userId: Long?,
    val fullName: String,
    val email: String,
    val role: Role?
)
