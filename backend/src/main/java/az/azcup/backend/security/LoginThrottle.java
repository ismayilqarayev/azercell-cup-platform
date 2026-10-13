package az.azcup.backend.security;

import az.azcup.backend.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

/**
 * Parol sınamasına qarşı: eyni (e-poçt, IP) cütü üçün 10 dəqiqə ərzində {@code login-failures} uğursuz cəhddən sonra
 * həmin cüt üçün giriş müvəqqəti bağlanır. Açar e-poçtu İP ilə birləşdirdiyi üçün başqa şəxs qurbanın hesabını öz
 * kompüterindən "kilidləyə" bilmir — yalnız öz IP-sindən olan cəhdlər bloklanır.
 */
@Component
public class LoginThrottle {

    private final boolean enabled;
    private final SlidingWindowLimiter failures;

    @Autowired
    public LoginThrottle(
        @Value("${app.rate-limit.enabled:true}") boolean enabled,
        @Value("${app.rate-limit.login-failures:8}") int maxFailures
    ) {
        this(enabled, maxFailures, System::currentTimeMillis);
    }

    LoginThrottle(boolean enabled, int maxFailures, LongSupplier clock) {
        this.enabled = enabled;
        this.failures = new SlidingWindowLimiter(maxFailures, TimeUnit.MINUTES.toMillis(10), clock);
    }

    public String key(String email, String ip) {
        return (email == null ? "" : email.trim().toLowerCase(Locale.ROOT)) + "|" + ip;
    }

    /** Cüt bloklanıbsa 429 ApiException atır. */
    public void ensureAllowed(String key) {
        if (!enabled) {
            return;
        }
        long wait = failures.retryAfterSeconds(key);
        if (wait > 0) {
            long minutes = (wait + 59) / 60;
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS,
                "Çox sayda uğursuz giriş cəhdi. " + minutes + " dəqiqədən sonra yenidən cəhd edin.");
        }
    }

    public void recordFailure(String key) {
        if (enabled) {
            failures.record(key);
        }
    }

    public void reset(String key) {
        failures.clear(key);
    }
}
