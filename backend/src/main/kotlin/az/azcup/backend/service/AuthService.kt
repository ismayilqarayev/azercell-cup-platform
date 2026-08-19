package az.azcup.backend.service

import az.azcup.backend.dto.auth.AuthResponse
import az.azcup.backend.dto.auth.LoginRequest
import az.azcup.backend.dto.auth.RegisterRequest
import az.azcup.backend.entity.Role
import az.azcup.backend.entity.User
import az.azcup.backend.exception.ApiException
import az.azcup.backend.exception.ConflictException
import az.azcup.backend.repository.UserRepository
import az.azcup.backend.security.JwtService
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

// Qeydiyyat və giriş (login) biznes-məntiqi. AuthController birbaşa bunu çağırır.
@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager
) {

    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw ConflictException("Bu email artıq qeydiyyatdan keçib")
        }
        // Rol göstərilməyibsə default STUDENT — açıq qeydiyyat forması
        // heç kimin özünü rol seçmədən "yüksək səlahiyyətli" edə bilməyəcəyi
        // ən təhlükəsiz defaultdur.
        val role = request.role ?: Role.STUDENT
        // ADMIN rolu ilə açıq qeydiyyat formasından qeydiyyatdan keçmək
        // MÜMKÜN DEYİL — yeganə admin AdminBootstrapRunner vasitəsilə
        // ilk başlanğıcda yaradılır, sonradan yalnız mövcud admin başqasını
        // admin edə bilər (bax: AdminController).
        if (role == Role.ADMIN) {
            throw ConflictException("Bu rolla qeydiyyatdan keçmək mümkün deyil")
        }
        // Şagirdlər dərhal təsdiqlənir, müəllimlər isə admin təsdiqini gözləməlidir
        // (saxta müəllim hesablarının qarşısını almaq üçün).
        val approved = role != Role.TEACHER
        val user = User(
            fullName = request.fullName,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password)!!,
            role = role,
            approved = approved
        )
        userRepository.save(user)
        if (!approved) {
            // Hesab yaradıldı, amma hələ giriş edə bilməz — token qaytarılmır,
            // sadəcə "qeydiyyat qəbul edildi, təsdiq gözlənilir" mənasında
            // istifadəçi məlumatları göstərilir.
            return AuthResponse(null, user.id, user.fullName, user.email, user.role)
        }
        return toAuthResponse(user)
    }

    fun login(request: LoginRequest): AuthResponse {
        // Bu çağırış daxildə CustomUserDetailsService-i işə salıb email/parolu
        // yoxlayır — uyğunsuzluq olarsa BadCredentialsException atır (bax:
        // GlobalExceptionHandler.handleBadCredentials).
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalStateException("İstifadəçi tapılmadı")
        // Parol düzgün olsa belə, təsdiqlənməmiş müəllim daxil ola bilmir.
        if (user.role == Role.TEACHER && !user.approved) {
            throw ApiException(HttpStatus.FORBIDDEN, "Hesabınız hələ admin tərəfindən təsdiqlənməyib.")
        }
        // Admin tərəfindən deaktiv edilmiş (bloklanmış) hesablar da rədd edilir.
        if (!user.active) {
            throw ApiException(HttpStatus.FORBIDDEN, "Hesabınız admin tərəfindən deaktiv edilib.")
        }
        return toAuthResponse(user)
    }

    // İstifadəçi üçün yeni JWT yaradıb, onu AuthResponse formasına çevirir.
    private fun toAuthResponse(user: User): AuthResponse =
        AuthResponse(
            jwtService.generateToken(user),
            user.id,
            user.fullName,
            user.email,
            user.role
        )
}
