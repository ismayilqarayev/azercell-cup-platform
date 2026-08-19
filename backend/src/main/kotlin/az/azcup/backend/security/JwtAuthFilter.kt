package az.azcup.backend.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

// Hər gələn HTTP sorğusunda BİR DƏFƏ (OncePerRequestFilter) işə düşən filtr.
// Vəzifəsi: "Authorization: Bearer <token>" header-ini oxumaq, tokeni
// doğrulamaq və uğurlu olarsa, bu sorğu üçün Spring Security-nin
// təhlükəsizlik kontekstinə (SecurityContextHolder) istifadəçini yazmaq —
// beləliklə controller-lərdəki @AuthenticationPrincipal annotasiyası
// avtomatik olaraq giriş etmiş istifadəçini əldə edə bilir.
@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            // "Bearer " prefiksini (7 simvol) ataraq xam tokeni ayırırıq.
            val token = header.substring(7)
            try {
                // getContext().authentication == null yoxlaması — əgər artıq
                // bu sorğu daxilində başqa bir mexanizm istifadəçini təyin
                // edibsə, üzərinə yazmırıq (təkrar işləmənin qarşısını alır).
                if (jwtService.isValid(token) && SecurityContextHolder.getContext().authentication == null) {
                    val email = jwtService.extractEmail(token)
                    val userDetails = userDetailsService.loadUserByUsername(email)
                    val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            } catch (e: Exception) {
                // Token korlanıb/etibarsızdırsa sorğunu rədd etmirik — sadəcə
                // kontekst boş qalır, sonrakı authorizeHttpRequests qaydaları
                // (bax: SecurityConfig) bunu "giriş edilməyib" kimi qəbul edib
                // lazım olan yerlərdə 401/403 qaytaracaq.
                SecurityContextHolder.clearContext()
            }
        }
        // Token olsun-olmasın, sorğu zənciri HƏMİŞƏ davam etdirilir — son
        // qərarı (icazə var/yox) SecurityConfig-dəki qaydalar verir.
        filterChain.doFilter(request, response)
    }
}
