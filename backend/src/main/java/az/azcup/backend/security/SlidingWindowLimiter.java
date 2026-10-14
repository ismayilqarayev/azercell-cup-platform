package az.azcup.backend.security;

import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongSupplier;

/**
 * "Sürüşən pəncərə" sürət limiti: hər açar (IP, istifadəçi, e-poçt+IP) üçün son {@code windowMillis} ərzindəki hadisələrin
 * vaxtlarını saxlayır; say {@code max}-a çatıbsa yeni hadisəyə icazə vermir. Yaddaşda işləyir (tək server nüsxəsi üçün kifayətdir),
 * köhnə açarlar vaxtaşırı təmizlənir ki, yaddaş böyüməsin.
 */
public final class SlidingWindowLimiter {

    private final int max;
    private final long windowMillis;
    private final LongSupplier clock;
    private final ConcurrentHashMap<String, ArrayDeque<Long>> events = new ConcurrentHashMap<>();
    private final AtomicInteger calls = new AtomicInteger();

    public SlidingWindowLimiter(int max, long windowMillis) {
        this(max, windowMillis, System::currentTimeMillis);
    }

    // Saat xaricdən verilir ki, testlərdə vaxtı "irəli çəkmək" mümkün olsun.
    public SlidingWindowLimiter(int max, long windowMillis, LongSupplier clock) {
        this.max = max;
        this.windowMillis = windowMillis;
        this.clock = clock;
    }

    /**
     * Hadisəni qeyd etməyə çalışır. {@code 0} qaytarırsa icazə verildi və hadisə sayıldı; müsbət ədəd qaytarırsa limit
     * dolub — bu qədər SANİYƏ sonra yenidən cəhd olunmalıdır.
     */
    public long tryAcquire(String key) {
        long[] retryAfter = {0};
        events.compute(key, (k, deque) -> {
            long now = clock.getAsLong();
            ArrayDeque<Long> window = deque == null ? new ArrayDeque<>() : deque;
            purge(window, now);
            if (window.size() >= max) {
                retryAfter[0] = secondsUntilFree(window, now);
            } else {
                window.addLast(now);
            }
            return window;
        });
        cleanupSometimes();
        return retryAfter[0];
    }

    /** Hadisəni limit yoxlamadan qeyd edir (məs. uğursuz giriş cəhdi). */
    public void record(String key) {
        events.compute(key, (k, deque) -> {
            long now = clock.getAsLong();
            ArrayDeque<Long> window = deque == null ? new ArrayDeque<>() : deque;
            purge(window, now);
            window.addLast(now);
            return window;
        });
        cleanupSometimes();
    }

    /** Qeyd etmədən yoxlayır: limit doludursa gözləmə saniyəsi, deyilsə 0. */
    public long retryAfterSeconds(String key) {
        long[] retryAfter = {0};
        events.computeIfPresent(key, (k, deque) -> {
            long now = clock.getAsLong();
            purge(deque, now);
            if (deque.size() >= max) {
                retryAfter[0] = secondsUntilFree(deque, now);
            }
            return deque.isEmpty() ? null : deque;
        });
        return retryAfter[0];
    }

    /** Açarın sayını sıfırlayır (məs. uğurlu girişdən sonra). */
    public void clear(String key) {
        events.remove(key);
    }

    private void purge(ArrayDeque<Long> window, long now) {
        while (!window.isEmpty() && now - window.peekFirst() >= windowMillis) {
            window.pollFirst();
        }
    }

    private long secondsUntilFree(ArrayDeque<Long> window, long now) {
        long freeAt = window.peekFirst() + windowMillis;
        return Math.max(1, (freeAt - now + 999) / 1000);
    }

    // Hər ~1000 çağırışdan bir müddəti bitmiş açarları silir (yaddaş sızmasının qarşısı). Deque-yə yalnız compute daxilində
    // toxunulur — ArrayDeque thread-safe deyil, ConcurrentHashMap isə eyni açar üçün compute-ları ardıcıl icra edir.
    private void cleanupSometimes() {
        if (calls.incrementAndGet() % 1000 != 0) {
            return;
        }
        for (String key : events.keySet()) {
            events.computeIfPresent(key, (k, deque) -> {
                purge(deque, clock.getAsLong());
                return deque.isEmpty() ? null : deque;
            });
        }
    }
}
