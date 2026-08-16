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

@Service
class JwtService(
    @Value("\${app.jwt.secret}") secret: String,
    @Value("\${app.jwt.expiration-minutes}") expirationMinutes: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    private val expirationMillis: Long = expirationMinutes * 60_000

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

    fun extractEmail(token: String): String = parseClaims(token).subject

    fun isValid(token: String): Boolean =
        try {
            val claims = parseClaims(token)
            claims.expiration.after(Date())
        } catch (e: Exception) {
            false
        }

    private fun parseClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}
