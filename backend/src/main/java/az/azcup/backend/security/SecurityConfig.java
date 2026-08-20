package az.azcup.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// Bütün HTTP təhlükəsizlik qaydalarının mərkəzi konfiqurasiyası:
// hansı endpoint-lərin açıq, hansının login tələb etdiyi, hansının
// yalnız müəyyən rola icazə verdiyi burada təyin olunur.
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Parollar bazaya HEÇ VAXT açıq mətn kimi yazılmır — BCrypt hər dəfə
    // fərqli "salt" istifadə edərək hash yaradır, ona görə eyni parol belə
    // bazada hər dəfə fərqli görünür (bu, "rainbow table" hücumlarına qarşı qorur).
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthService-in login zamanı email+parolu yoxlamaq üçün istifadə etdiyi bean.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF qorunması cookie-əsaslı sessiyalarda vacibdir — bizdə isə
            // JWT token hər sorğuda Authorization header-i ilə göndərilir
            // (cookie yoxdur), ona görə CSRF riski aradan qalxır və deaktiv edilə bilər.
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // STATELESS — server heç bir sessiya saxlamır, hər sorğu özü ilə
            // gətirdiyi JWT token vasitəsilə tam müstəqil autentifikasiya olunur.
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Qeydiyyat və giriş hər kəsə açıqdır (hələ token yoxdur ki).
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                // İstifadəçi siyahısını həm ADMIN, həm TEACHER görə bilər (yoxlama üçün),
                // amma dəyişiklik etmək (POST/PUT/DELETE) yalnız ADMIN-ə açıqdır (aşağıdakı qayda).
                .requestMatchers(HttpMethod.GET, "/api/admin/users").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/teacher/**").hasAnyRole("TEACHER", "ADMIN")
                // Qalan bütün /api/** endpoint-ləri ən azı giriş etmiş
                // (istənilən rol) istifadəçi tələb edir.
                .requestMatchers("/api/**").authenticated()
                // /api/ altında olmayan hər şey (statik frontend faylları və s.)
                // sərbəst açıqdır.
                .anyRequest().permitAll()
            )
            // Öz JWT filtrimizi Spring-in standart
            // istifadəçi adı/parol filtrindən ƏVVƏL işə salırıq ki, hər sorğuda
            // əvvəlcə token yoxlanılsın (bax: JwtAuthFilter).
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // Frontend fərqli origin-dən (fərqli port/domen) API-ni çağıra bilsin deyə
    // CORS icazələri. allowedOriginPatterns = "*" — istənilən mənbədən sorğuya
    // icazə verir (bu layihə üçün qəbul edilə bilər, çünki cookie/kredensial
    // paylaşılmır — allowCredentials = false).
    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
