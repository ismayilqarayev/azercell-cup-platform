package az.azcup.backend.dto.auth

import az.azcup.backend.entity.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// "POST /api/auth/register" gövdəsi. Parol üçün minimum 8 simvol tələbi
// @Size ilə tətbiq olunur. role sahəsi nullable-dır — AuthService-də,
// göndərilməyibsə default olaraq STUDENT təyin edilir (bax: AuthService.register).
data class RegisterRequest(
    @field:NotBlank val fullName: String,
    @field:NotBlank @field:Email val email: String,
    @field:NotBlank @field:Size(min = 8, max = 100) val password: String,
    val role: Role?
)
