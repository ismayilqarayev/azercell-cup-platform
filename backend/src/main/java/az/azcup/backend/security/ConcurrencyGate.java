package az.azcup.backend.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Açar başına (istifadəçi) eyni anda ən çox {@code max} icraya icazə verir. {@link #tryEnter} uğurlu olduqda mütləq
 * {@link #exit} çağırılmalıdır (finally-də).
 */
public final class ConcurrencyGate {

    private final ConcurrentHashMap<String, AtomicInteger> active = new ConcurrentHashMap<>();

    public boolean tryEnter(String key, int max) {
        boolean[] entered = {false};
        active.compute(key, (k, counter) -> {
            AtomicInteger c = counter == null ? new AtomicInteger() : counter;
            if (c.get() < max) {
                c.incrementAndGet();
                entered[0] = true;
            }
            return c;
        });
        return entered[0];
    }

    public void exit(String key) {
        active.computeIfPresent(key, (k, counter) -> counter.decrementAndGet() <= 0 ? null : counter);
    }

    /** Test üçün: açar üzrə hazırda aktiv icra sayı. */
    int activeCount(String key) {
        AtomicInteger c = active.get(key);
        return c == null ? 0 : c.get();
    }
}
