package az.azcup.backend.service;

import az.azcup.backend.dto.auth.AuthResponse;
import az.azcup.backend.dto.auth.LoginRequest;
import az.azcup.backend.dto.auth.RegisterRequest;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import az.azcup.backend.exception.ApiException;
import az.azcup.backend.exception.ConflictException;
import az.azcup.backend.repository.UserRepository;
import az.azcup.backend.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Qeydiyyat və giriş (login) biznes-məntiqi. AuthController birbaşa bunu çağırır.
@Service
public class AuthService {

    // İstifadəçiləri oxumaq/yazmaq üçün.
    private final UserRepository userRepository;
    // Parolları hash-ləmək üçün.
    private final PasswordEncoder passwordEncoder;
    // Uğurlu giriş/qeydiyyatdan sonra JWT yaratmaq üçün.
    private final JwtService jwtService;
    // Login zamanı email+parolu Spring Security vasitəsilə doğrulamaq üçün.
    private final AuthenticationManager authenticationManager;

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // Yeni istifadəçi qeydiyyatdan keçirir; rol qaydalarını tətbiq edir və
    // (müəllim deyilsə) dərhal giriş üçün token qaytarır.
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Bu email artıq qeydiyyatdan keçib");
        }
        // Rol göstərilməyibsə default STUDENT — açıq qeydiyyat forması
        // heç kimin özünü rol seçmədən "yüksək səlahiyyətli" edə bilməyəcəyi
        // ən təhlükəsiz defaultdur.
        Role role;
        if (request.getRole() != null) {
            role = request.getRole();
        } else {
            role = Role.STUDENT;
        }
        // ADMIN rolu ilə açıq qeydiyyat formasından qeydiyyatdan keçmək
        // MÜMKÜN DEYİL — yeganə admin AdminBootstrapRunner vasitəsilə
        // ilk başlanğıcda yaradılır, sonradan yalnız mövcud admin başqasını
        // admin edə bilər (bax: AdminController).
        if (role == Role.ADMIN) {
            throw new ConflictException("Bu rolla qeydiyyatdan keçmək mümkün deyil");
        }
        // Şagirdlər dərhal təsdiqlənir, müəllimlər isə admin təsdiqini gözləməlidir
        // (saxta müəllim hesablarının qarşısını almaq üçün).
        boolean approved = role != Role.TEACHER;
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setApproved(approved);
        userRepository.save(user);
        if (!approved) {
            // Hesab yaradıldı, amma hələ giriş edə bilməz — token qaytarılmır,
            // sadəcə "qeydiyyat qəbul edildi, təsdiq gözlənilir" mənasında
            // istifadəçi məlumatları göstərilir.
            return new AuthResponse(null, user.getId(), user.getFullName(), user.getEmail(), user.getRole());
        }
        return toAuthResponse(user);
    }

    // E-poçt/parolu doğrulayır və hesabın giriş üçün əlverişli (təsdiqlənmiş,
    // aktiv) olduğunu yoxlayaraq JWT token qaytarır.
    public AuthResponse login(LoginRequest request) {
        // Bu çağırış daxildə CustomUserDetailsService-i işə salıb email/parolu
        // yoxlayır — uyğunsuzluq olarsa BadCredentialsException atır (bax:
        // GlobalExceptionHandler.handleBadCredentials).
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new IllegalStateException("İstifadəçi tapılmadı");
        }
        // Parol düzgün olsa belə, təsdiqlənməmiş müəllim daxil ola bilmir.
        if (user.getRole() == Role.TEACHER && !user.isApproved()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Hesabınız hələ admin tərəfindən təsdiqlənməyib.");
        }
        // Admin tərəfindən deaktiv edilmiş (bloklanmış) hesablar da rədd edilir.
        if (!user.isActive()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Hesabınız admin tərəfindən deaktiv edilib.");
        }
        return toAuthResponse(user);
    }

    // İstifadəçi üçün yeni JWT yaradıb, onu AuthResponse formasına çevirir.
    private AuthResponse toAuthResponse(User user) {
        return new AuthResponse(
            jwtService.generateToken(user),
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole()
        );
    }
}
