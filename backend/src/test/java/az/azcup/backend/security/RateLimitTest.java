package az.azcup.backend.security;

import az.azcup.backend.exception.ApiException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Sürət limitinin mexanikası: sürüşən pəncərə, eyni anda icra qapısı, sorğu filtri və giriş cəhdi kilidi.
class RateLimitTest {

    private final AtomicLong now = new AtomicLong(1_000_000);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ---- SlidingWindowLimiter

    @Test
    void limiterAllowsUpToMaxThenBlocksUntilTheWindowSlides() {
        SlidingWindowLimiter limiter = new SlidingWindowLimiter(3, 60_000, now::get);

        assertThat(limiter.tryAcquire("k")).isZero();
        assertThat(limiter.tryAcquire("k")).isZero();
        assertThat(limiter.tryAcquire("k")).isZero();
        assertThat(limiter.tryAcquire("k")).isEqualTo(60);   // doludur: ilk hadisə 60 san sonra düşür

        now.addAndGet(20_000);
        assertThat(limiter.tryAcquire("k")).isEqualTo(40);

        now.addAndGet(40_000);                                // ilk hadisə pəncərədən çıxdı
        assertThat(limiter.tryAcquire("k")).isZero();
    }

    @Test
    void limiterKeysAreIndependentAndClearable() {
        SlidingWindowLimiter limiter = new SlidingWindowLimiter(1, 60_000, now::get);

        assertThat(limiter.tryAcquire("a")).isZero();
        assertThat(limiter.tryAcquire("b")).isZero();
        assertThat(limiter.tryAcquire("a")).isPositive();

        limiter.clear("a");
        assertThat(limiter.tryAcquire("a")).isZero();
    }

    @Test
    void retryAfterDoesNotConsumeASlot() {
        SlidingWindowLimiter limiter = new SlidingWindowLimiter(2, 60_000, now::get);
        limiter.record("k");

        assertThat(limiter.retryAfterSeconds("k")).isZero();
        assertThat(limiter.retryAfterSeconds("k")).isZero();    // yoxlama sayğacı artırmır
        limiter.record("k");
        assertThat(limiter.retryAfterSeconds("k")).isEqualTo(60);
    }

    // ---- ConcurrencyGate

    @Test
    void gateAllowsOnlyMaxConcurrentEntriesPerKey() {
        ConcurrencyGate gate = new ConcurrencyGate();

        assertThat(gate.tryEnter("u", 1)).isTrue();
        assertThat(gate.tryEnter("u", 1)).isFalse();
        assertThat(gate.tryEnter("v", 1)).isTrue();   // başqa açar təsirlənmir

        gate.exit("u");
        assertThat(gate.activeCount("u")).isZero();
        assertThat(gate.tryEnter("u", 1)).isTrue();
    }

    // ---- RateLimitFilter

    private RateLimitFilter filter() {
        return new RateLimitFilter(true, 3, 2, 2, 5, now::get);
    }

    private MockHttpServletRequest post(String path) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", path);
        request.setRemoteAddr("10.0.0.7");
        return request;
    }

    private void authenticateAs(String email) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(email, null, List.of()));
    }

    @Test
    void loginIsLimitedPerIpWith429AndRetryAfter() throws Exception {
        RateLimitFilter filter = filter();
        for (int i = 0; i < 3; i++) {
            MockHttpServletResponse ok = new MockHttpServletResponse();
            filter.doFilter(post("/api/auth/login"), ok, new MockFilterChain());
            assertThat(ok.getStatus()).isEqualTo(200);
        }
        MockHttpServletResponse blocked = new MockHttpServletResponse();
        filter.doFilter(post("/api/auth/login"), blocked, new MockFilterChain());

        assertThat(blocked.getStatus()).isEqualTo(429);
        assertThat(blocked.getHeader("Retry-After")).isEqualTo("600");
        assertThat(blocked.getContentAsString()).contains("\"status\":429").contains("message");

        // Başqa IP təsirlənmir.
        MockHttpServletRequest other = post("/api/auth/login");
        other.setRemoteAddr("10.0.0.8");
        MockHttpServletResponse otherResponse = new MockHttpServletResponse();
        filter.doFilter(other, otherResponse, new MockFilterChain());
        assertThat(otherResponse.getStatus()).isEqualTo(200);
    }

    @Test
    void getRequestsAndOtherPathsAreNeverLimited() throws Exception {
        RateLimitFilter filter = filter();
        for (int i = 0; i < 20; i++) {
            MockHttpServletResponse get = new MockHttpServletResponse();
            filter.doFilter(new MockHttpServletRequest("GET", "/api/auth/login"), get, new MockFilterChain());
            MockHttpServletResponse other = new MockHttpServletResponse();
            filter.doFilter(post("/api/problems"), other, new MockFilterChain());
            assertThat(get.getStatus()).isEqualTo(200);
            assertThat(other.getStatus()).isEqualTo(200);
        }
    }

    @Test
    void codeRunIsLimitedToOneAtATimePerUser() throws Exception {
        RateLimitFilter filter = filter();
        authenticateAs("student@azcup.local");
        MockHttpServletResponse nested = new MockHttpServletResponse();

        // Birinci icra gedərkən (chain içində) eyni istifadəçi ikinci icra göndərir.
        MockHttpServletResponse first = new MockHttpServletResponse();
        filter.doFilter(post("/api/run"), first, (req, res) -> {
            try {
                filter.doFilter(post("/api/run"), nested, new MockFilterChain());
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });

        assertThat(first.getStatus()).isEqualTo(200);
        assertThat(nested.getStatus()).isEqualTo(429);
        assertThat(nested.getContentAsString()).contains("hələ yoxlanılır");

        // Birinci bitəndən sonra qapı açılır.
        MockHttpServletResponse after = new MockHttpServletResponse();
        filter.doFilter(post("/api/run"), after, new MockFilterChain());
        assertThat(after.getStatus()).isEqualTo(200);
    }

    @Test
    void differentUsersDoNotBlockEachOther() throws Exception {
        RateLimitFilter filter = filter();
        MockHttpServletResponse nested = new MockHttpServletResponse();

        authenticateAs("one@azcup.local");
        filter.doFilter(post("/api/problems/5/submissions"), new MockHttpServletResponse(), (req, res) -> {
            authenticateAs("two@azcup.local");
            try {
                filter.doFilter(post("/api/contests/problems/9/submissions"), nested, new MockFilterChain());
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });

        assertThat(nested.getStatus()).isEqualTo(200);
    }

    @Test
    void codeRunHasAPerMinuteCapAndRecoversAfterTheWindow() throws Exception {
        RateLimitFilter filter = filter();   // run-per-user = 2
        authenticateAs("student@azcup.local");
        for (int i = 0; i < 2; i++) {
            MockHttpServletResponse ok = new MockHttpServletResponse();
            filter.doFilter(post("/api/run"), ok, new MockFilterChain());
            assertThat(ok.getStatus()).isEqualTo(200);
        }
        MockHttpServletResponse blocked = new MockHttpServletResponse();
        filter.doFilter(post("/api/run"), blocked, new MockFilterChain());
        assertThat(blocked.getStatus()).isEqualTo(429);

        now.addAndGet(61_000);
        MockHttpServletResponse again = new MockHttpServletResponse();
        filter.doFilter(post("/api/run"), again, new MockFilterChain());
        assertThat(again.getStatus()).isEqualTo(200);
    }

    @Test
    void disabledFilterLetsEverythingThrough() throws Exception {
        RateLimitFilter off = new RateLimitFilter(false, 1, 1, 1, 1, now::get);
        for (int i = 0; i < 5; i++) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            off.doFilter(post("/api/auth/login"), response, new MockFilterChain());
            assertThat(response.getStatus()).isEqualTo(200);
        }
    }

    // ---- LoginThrottle

    @Test
    void loginIsLockedAfterTooManyFailuresForThatEmailAndIpOnly() {
        LoginThrottle throttle = new LoginThrottle(true, 3, now::get);
        String victimFromAttacker = throttle.key("Victim@Azcup.local", "203.0.113.9");
        String victimFromHome = throttle.key("victim@azcup.local", "198.51.100.4");

        for (int i = 0; i < 3; i++) {
            throttle.ensureAllowed(victimFromAttacker);
            throttle.recordFailure(victimFromAttacker);
        }

        assertThatThrownBy(() -> throttle.ensureAllowed(victimFromAttacker))
            .isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS));
        // Qurbanın öz IP-sindən giriş bloklanmır (e-poçt hərf registrindən asılı deyil, IP fərqlidir).
        throttle.ensureAllowed(victimFromHome);

        now.addAndGet(10 * 60_000 + 1_000);
        throttle.ensureAllowed(victimFromAttacker);   // pəncərə keçdi
    }

    @Test
    void successfulLoginResetsTheFailureCount() {
        LoginThrottle throttle = new LoginThrottle(true, 2, now::get);
        String key = throttle.key("student@azcup.local", "10.0.0.7");

        throttle.recordFailure(key);
        throttle.reset(key);
        throttle.recordFailure(key);
        throttle.ensureAllowed(key);    // reset olunduğu üçün cəmi 1 uğursuz cəhd var, limit 2-dir
    }
}
