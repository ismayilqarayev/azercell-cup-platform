package az.azcup.backend.security

import az.azcup.backend.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

// Bizim User entity-mizi Spring Security-nin daxili UserDetails
// interfeysinə "bükən" (wrap edən) adapter. Controller-lərdə
// @AuthenticationPrincipal ilə əldə edilən obyekt məhz budur — .user
// vasitəsilə əsl User entity-sinə çatmaq olur (bax: SubmissionController və s.).
class UserPrincipal(val user: User) : UserDetails {

    // Rol adını "ROLE_" prefiksi ilə qaytarır (məs. "ROLE_ADMIN") — Spring
    // Security-nin hasRole()/hasAnyRole() yoxlamaları məhz bu formatı gözləyir.
    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${user.role?.name}"))

    override fun getPassword(): String = user.passwordHash

    // Bizdə "username" kimi e-poçt istifadə olunur.
    override fun getUsername(): String = user.email
}
