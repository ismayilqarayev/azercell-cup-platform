package az.azcup.backend.security

import az.azcup.backend.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

// Spring Security-nin standart UserDetailsService kontraktının bizim User
// entity-mizə bağlanması. Login zamanı (AuthenticationManager vasitəsilə)
// və hər JWT-li sorğuda (JwtAuthFilter vasitəsilə) çağırılır.
@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    // Bizdə "username" əvəzinə e-poçt istifadə olunur (login sahəsi email-dir).
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("No user with email $email")
        // Öz User entity-mizi Spring Security-nin gözlədiyi UserDetails
        // interfeysinə uyğunlaşdıran "adapter" (bax: UserPrincipal).
        return UserPrincipal(user)
    }
}
