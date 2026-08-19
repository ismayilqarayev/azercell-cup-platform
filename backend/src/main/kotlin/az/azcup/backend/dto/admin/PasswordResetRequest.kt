package az.azcup.backend.dto.admin

// "POST /api/admin/users/{id}/reset-password" gövdəsi. newPassword
// göndərilməzsə (null), AdminService özü təsadüfi güclü parol yaradır
// (bax: AdminService.resetPassword) — admin istəyə görə ya öz parolunu
// təyin edə, ya da sistemə buraxa bilər.
data class PasswordResetRequest(
    val newPassword: String?
)
