package az.azcup.backend.security;

import az.azcup.backend.entity.User;
import az.azcup.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security-nin standart UserDetailsService kontraktının bizim User
// entity-mizə bağlanması. Login zamanı (AuthenticationManager vasitəsilə)
// və hər JWT-li sorğuda (JwtAuthFilter vasitəsilə) çağırılır.
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Bizdə "username" əvəzinə e-poçt istifadə olunur (login sahəsi email-dir).
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("No user with email " + email);
        }
        // Öz User entity-mizi Spring Security-nin gözlədiyi UserDetails
        // interfeysinə uyğunlaşdıran "adapter" (bax: UserPrincipal).
        return new UserPrincipal(user);
    }
}
