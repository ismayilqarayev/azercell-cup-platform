package az.azcup.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Ən sadə "smoke test" — Spring konteksti (bütün bean-lar) heç bir xəta
// olmadan tam yüklənə bilirmi, sadəcə bunu yoxlayır. Konkret bir davranışı
// yox, "tətbiq ümumiyyətlə başlaya bilirmi" sualını cavablandırır.
@SpringBootTest
class BackendApplicationTests {

    // Boş test gövdəsi kifayətdir — @SpringBootTest kontekstin yüklənməsini
    // özü sınayır, əlavə assertion-a ehtiyac yoxdur (kontekst yüklənə
    // bilməsə, test avtomatik uğursuz olar).
    @Test
    void contextLoads() {
    }
}
