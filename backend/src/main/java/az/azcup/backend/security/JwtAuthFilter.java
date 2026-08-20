package az.azcup.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Hər gələn HTTP sorğusunda BİR DƏFƏ (OncePerRequestFilter) işə düşən filtr.
// Vəzifəsi: "Authorization: Bearer <token>" header-ini oxumaq, tokeni
// doğrulamaq və uğurlu olarsa, bu sorğu üçün Spring Security-nin
// təhlükəsizlik kontekstinə (SecurityContextHolder) istifadəçini yazmaq —
// beləliklə controller-lərdəki @AuthenticationPrincipal annotasiyası
// avtomatik olaraq giriş etmiş istifadəçini əldə edə bilir.
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    // Tokeni doğrulamaq və içindəki e-poçtu çıxarmaq üçün.
    private final JwtService jwtService;
    // Tokendəki e-poçta uyğun istifadəçini yükləmək üçün.
    private final CustomUserDetailsService userDetailsService;

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    // Hər sorğuda bir dəfə işə düşür — Authorization header-indəki JWT-ni
    // oxuyub doğrulayır və uğurlu olarsa istifadəçini SecurityContext-ə yazır.
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            // "Bearer " prefiksini (7 simvol) ataraq xam tokeni ayırırıq.
            String token = header.substring(7);
            try {
                // getAuthentication() == null yoxlaması — əgər artıq
                // bu sorğu daxilində başqa bir mexanizm istifadəçini təyin
                // edibsə, üzərinə yazmırıq (təkrar işləmənin qarşısını alır).
                if (jwtService.isValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    String email = jwtService.extractEmail(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                // Token korlanıb/etibarsızdırsa sorğunu rədd etmirik — sadəcə
                // kontekst boş qalır, sonrakı authorizeHttpRequests qaydaları
                // (bax: SecurityConfig) bunu "giriş edilməyib" kimi qəbul edib
                // lazım olan yerlərdə 401/403 qaytaracaq.
                SecurityContextHolder.clearContext();
            }
        }
        // Token olsun-olmasın, sorğu zənciri HƏMİŞƏ davam etdirilir — son
        // qərarı (icazə var/yox) SecurityConfig-dəki qaydalar verir.
        filterChain.doFilter(request, response);
    }
}
