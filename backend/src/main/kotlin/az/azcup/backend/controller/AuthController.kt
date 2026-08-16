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

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request))

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse = authService.login(request)

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun me(@AuthenticationPrincipal principal: UserPrincipal): MeResponse {
        val user = principal.user
        return MeResponse(user.id, user.fullName, user.email, user.role)
    }

    data class MeResponse(val id: Long?, val fullName: String, val email: String, val role: Role?)
}
