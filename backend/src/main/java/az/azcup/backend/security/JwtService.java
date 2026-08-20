package az.azcup.backend.security;

import az.azcup.backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

// JWT (JSON Web Token) yaratmaq və doğrulamaq üçün mərkəzi servis.
// Token, istifadəçinin server-də sessiya saxlamadan (stateless) autentifikasiya
// olunmasına imkan verir: server tokeni imzalayır, müştəri onu hər sorğuda
// göndərir, server isə yalnız imzanı yoxlayaraq etibarlılığını təsdiqləyir.
@Service
public class JwtService {

    // HMAC-SHA açarı — secret mətnindən bir dəfə hesablanır və yaddaşda saxlanılır
    // (hər dəfə yenidən yaratmağa ehtiyac yoxdur).
    private final SecretKey key;
    private final long expirationMillis;

    public JwtService(
        // JWT_SECRET environment dəyişənindən gəlir (bax: application.yml) —
        // bu, tokenləri imzalamaq üçün istifadə olunan məxfi açardır. Əgər bu
        // sızarsa, istənilən şəxs saxta token yarada bilər, ona görə production-da
        // mütləq real, təsadüfi (256-bit) dəyərlə əvəz olunmalıdır.
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMinutes * 60_000;
    }

    // Uğurlu login/qeydiyyatdan sonra istifadəçi üçün imzalanmış token yaradır.
    // Token daxilində: subject=email, uid və role claim-ləri, yaranma/bitmə vaxtı.
    public String generateToken(User user) {
        Instant now = Instant.now();
        String roleName;
        if (user.getRole() != null) {
            roleName = user.getRole().name();
        } else {
            roleName = null;
        }
        return Jwts.builder()
            .subject(user.getEmail())
            .claim("uid", user.getId())
            .claim("role", roleName)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMillis)))
            .signWith(key)
            .compact();
    }

    // Tokendən (imza yoxlanıldıqdan sonra) e-poçt ünvanını çıxarır —
    // JwtAuthFilter bunu istifadəçini yenidən yükləmək üçün istifadə edir.
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    // Tokenin həm imzasının düzgün olduğunu, həm də vaxtının bitmədiyini
    // yoxlayır. İstənilən problem (saxta imza, korlanmış format, bitmiş vaxt)
    // sadəcə "etibarsız" (false) kimi qəbul edilir, xəta atılmır.
    public boolean isValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Tokeni açıb daxilindəki claim-ləri (subject, uid, role və s.) qaytarır.
    // İmza yanlışdırsa (token saxtalaşdırılıbsa) bu metod istisna atır.
    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
