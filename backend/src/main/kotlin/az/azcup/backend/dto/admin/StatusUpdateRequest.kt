package az.azcup.backend.dto.admin

// Admin panelində bir istifadəçini aktiv/deaktiv etmək üçün
// ("PUT /api/admin/users/{id}/status") — deaktiv istifadəçi giriş edə bilmir
// (bax: AuthService.login), amma tarixçəsi silinmir.
data class StatusUpdateRequest(
    val active: Boolean
)
