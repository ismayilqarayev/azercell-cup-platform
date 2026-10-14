package az.azcup.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import java.util.regex.Pattern;

/**
 * Açıq qeydiyyat + serverdə kod icrası olan platformada sui-istifadəni məhdudlaşdırır (bax: JudgeSandbox):
 * <ul>
 *   <li>{@code POST /api/auth/login} və {@code /register}: IP başına limit (sinif eyni məktəb IP-sindən girdiyi üçün
 *       limitlər səxavətlidir; yalnız avtomatlaşdırılmış seli kəsir). Hesab üzrə parol cəhdləri ayrıca {@link LoginThrottle}
 *       ilə məhdudlaşır.</li>
 *   <li>Kod icra edən endpoint-lər ({@code /api/run}, məsələ və müsabiqə göndərmələri): istifadəçi başına eyni anda 1 icra və
 *       dəqiqədə ən çox {@code run-per-user} — bir nəfər bütün yoxlayıcı uid hovuzunu tutub başqalarını əngəlləməsin.</li>
 *   <li>{@code POST /api/run/check} (canlı sintaksis yoxlaması): yüngüldür, ona görə daha yüksək limit, eyni anda 2.</li>
 * </ul>
 * Limit aşılanda 429 və {@code Retry-After} qaytarılır. Müştəri IP-si {@code request.getRemoteAddr()}-dır; Caddy arxasında
 * {@code server.forward-headers-strategy=framework} onu X-Forwarded-For-dan düzgün təyin edir.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final Pattern PROBLEM_SUBMIT = Pattern.compile("^/api/problems/[^/]+/submissions/?$");
    private static final Pattern CONTEST_SUBMIT = Pattern.compile("^/api/contests/problems/[^/]+/submissions/?$");

    private final boolean enabled;
    private final SlidingWindowLimiter loginPerIp;
    private final SlidingWindowLimiter registerPerIp;
    private final SlidingWindowLimiter runPerUser;
    private final SlidingWindowLimiter checkPerUser;
    private final ConcurrencyGate gate = new ConcurrencyGate();

    @Autowired
    public RateLimitFilter(
        @Value("${app.rate-limit.enabled:true}") boolean enabled,
        @Value("${app.rate-limit.login-per-ip:200}") int loginPerIp,
        @Value("${app.rate-limit.register-per-ip:100}") int registerPerIp,
        @Value("${app.rate-limit.run-per-user:20}") int runPerUser,
        @Value("${app.rate-limit.check-per-user:120}") int checkPerUser
    ) {
        this(enabled, loginPerIp, registerPerIp, runPerUser, checkPerUser, System::currentTimeMillis);
    }

    // Saat xaricdən verilir ki, testlərdə vaxtı idarə etmək mümkün olsun.
    RateLimitFilter(boolean enabled, int loginPerIp, int registerPerIp, int runPerUser, int checkPerUser, LongSupplier clock) {
        this.enabled = enabled;
        this.loginPerIp = new SlidingWindowLimiter(loginPerIp, TimeUnit.MINUTES.toMillis(10), clock);
        this.registerPerIp = new SlidingWindowLimiter(registerPerIp, TimeUnit.HOURS.toMillis(1), clock);
        this.runPerUser = new SlidingWindowLimiter(runPerUser, TimeUnit.MINUTES.toMillis(1), clock);
        this.checkPerUser = new SlidingWindowLimiter(checkPerUser, TimeUnit.MINUTES.toMillis(1), clock);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        if (!enabled || !"POST".equals(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();

        if ("/api/auth/login".equals(path)) {
            if (deny(response, loginPerIp.tryAcquire("ip:" + request.getRemoteAddr()), "Çox sayda giriş cəhdi. Bir az sonra yenidən cəhd edin.")) {
                return;
            }
        } else if ("/api/auth/register".equals(path)) {
            if (deny(response, registerPerIp.tryAcquire("ip:" + request.getRemoteAddr()), "Çox sayda qeydiyyat cəhdi. Bir az sonra yenidən cəhd edin.")) {
                return;
            }
        } else if ("/api/run/check".equals(path)) {
            runLimited(request, response, chain, checkPerUser, 2, "chk");
            return;
        } else if ("/api/run".equals(path) || PROBLEM_SUBMIT.matcher(path).matches() || CONTEST_SUBMIT.matcher(path).matches()) {
            runLimited(request, response, chain, runPerUser, 1, "run");
            return;
        }
        chain.doFilter(request, response);
    }

    // Kod icra edən sorğu: dəqiqəlik limit + eyni anda icra sayı (açar: istifadəçi, giriş edilməyibsə IP).
    private void runLimited(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                            SlidingWindowLimiter perMinute, int maxConcurrent, String kind) throws ServletException, IOException {
        String key = kind + ":" + subject(request);
        // Əvvəl eyni anda icra qapısı: ikiqat klikləmə ilə rədd olunan sorğu dəqiqəlik limitdən pay götürməsin.
        if (!gate.tryEnter(key, maxConcurrent)) {
            deny(response, 1, "Əvvəlki kodunuz hələ yoxlanılır — bitməsini gözləyin.");
            return;
        }
        try {
            if (deny(response, perMinute.tryAcquire(key), "Çox tez-tez kod göndərirsiniz. Bir az gözləyin.")) {
                return;
            }
            chain.doFilter(request, response);
        } finally {
            gate.exit(key);
        }
    }

    // İstifadəçinin e-poçtu; giriş edilməyibsə (nəzəri hal — bu endpoint-lər autentifikasiya tələb edir) IP.
    private String subject(HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            return "u:" + auth.getName();
        }
        return "ip:" + request.getRemoteAddr();
    }

    // retryAfterSeconds > 0 olarsa 429 yazır və true qaytarır (sorğu dayandırılır).
    private boolean deny(HttpServletResponse response, long retryAfterSeconds, String message) throws IOException {
        if (retryAfterSeconds <= 0) {
            return false;
        }
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setHeader("Retry-After", Long.toString(retryAfterSeconds));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // GlobalExceptionHandler ilə eyni format: {status, error, message}.
        response.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"" + message + "\"}");
        return true;
    }
}
