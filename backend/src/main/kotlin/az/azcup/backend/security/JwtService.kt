package az.azcup.backend.security

import az.azcup.backend.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

// JWT (JSON Web Token) yaratmaq və doğrulamaq üçün mərkəzi servis.
// Token, istifadəçinin server-də sessiya saxlamadan (stateless) autentifikasiya
// olunmasına imkan verir: server tokeni imzalayır, müştəri onu hər sorğuda
// göndərir, server isə yalnız imzanı yoxlayaraq etibarlılığını təsdiqləyir.
@Service
class JwtService(
    // JWT_SECRET environment dəyişənindən gəlir (bax: application.yml) —
    // bu, tokenləri imzalamaq üçün istifadə olunan məxfi açardır. Əgər bu
    // sızarsa, istənilən şəxs saxta token yarada bilər, ona görə production-da
    // mütləq real, təsadüfi (256-bit) dəyərlə əvəz olunmalıdır.
    @Value("\${app.jwt.secret}") secret: String,
    @Value("\${app.jwt.expiration-minutes}") expirationMinutes: Long
) {
    // HMAC-SHA açarı — secret mətnindən bir dəfə hesablanır və yaddaşda saxlanılır
    // (hər dəfə yenidən yaratmağa ehtiyac yoxdur).
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    private val expirationMillis: Long = expirationMinutes * 60_000

    // Uğurlu login/qeydiyyatdan sonra istifadəçi üçün imzalanmış token yaradır.
    // Token daxilində: subject=email, uid və role claim-ləri, yaranma/bitmə vaxtı.
    fun generateToken(user: User): String {
        val now = Instant.now()
        return Jwts.builder()
            .subject(user.email)
            .claim("uid", user.id)
            .claim("role", user.role?.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(expirationMillis)))
            .signWith(key)
            .compact()
    }

    // Tokendən (imza yoxlanıldıqdan sonra) e-poçt ünvanını çıxarır —
    // JwtAuthFilter bunu istifadəçini yenidən yükləmək üçün istifadə edir.
    fun extractEmail(token: String): String = parseClaims(token).subject

    // Tokenin həm imzasının düzgün olduğunu, həm də vaxtının bitmədiyini
    // yoxlayır. İstənilən problem (saxta imza, korlanmış format, bitmiş vaxt)
    // sadəcə "etibarsız" (false) kimi qəbul edilir, xəta atılmır.
    fun isValid(token: String): Boolean =
        try {
            val claims = parseClaims(token)
            claims.expiration.after(Date())
        } catch (e: Exception) {
            false
        }

    // Tokeni açıb daxilindəki claim-ləri (subject, uid, role və s.) qaytarır.
    // İmza yanlışdırsa (token saxtalaşdırılıbsa) bu metod istisna atır.
    private fun parseClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}
