package az.azcup.backend.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

// "POST /api/auth/login" gövdəsi. @Email annotasiyası əsas format
// yoxlaması aparır (real e-poçt olub-olmadığını dəqiq təsdiqləmir,
// sadəcə "@" və domen olub-olmadığını yoxlayır) — əsl doğrulama
// AuthService-də parol/istifadəçi müqayisəsi ilə baş verir.
data class LoginRequest(
    @field:NotBlank @field:Email val email: String,
    @field:NotBlank val password: String
)
