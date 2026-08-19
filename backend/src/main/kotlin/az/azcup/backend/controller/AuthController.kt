package az.azcup.backend.controller

import az.azcup.backend.dto.auth.AuthResponse
import az.azcup.backend.dto.auth.LoginRequest
import az.azcup.backend.dto.auth.RegisterRequest
import az.azcup.backend.entity.Role
import az.azcup.backend.security.UserPrincipal
import az.azcup.backend.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// Qeydiyyat, giriş və "mən kiməm" sorğusu — yeganə tam AÇIQ (login tələb
// etməyən) endpoint-lər /register və /login-dir (bax: SecurityConfig).
@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request))

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse = authService.login(request)

    // Frontend səhifə açılanda (yenilənəndə) hazırkı istifadəçinin kim
    // olduğunu bilmək üçün çağırır — token localStorage-da saxlanılır,
    // amma "bu token kimə aiddir və hələ etibarlıdırmı" sualının cavabı
    // yalnız server-dən gələ bilər.
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun me(@AuthenticationPrincipal principal: UserPrincipal): MeResponse {
        val user = principal.user
        return MeResponse(user.id, user.fullName, user.email, user.role)
    }

    // Kiçik, yalnız bu endpoint-ə aid cavab tipi olduğu üçün ayrıca fayl
    // yaratmaq əvəzinə birbaşa controller daxilində təyin olunub.
    data class MeResponse(val id: Long?, val fullName: String, val email: String, val role: Role?)
}
