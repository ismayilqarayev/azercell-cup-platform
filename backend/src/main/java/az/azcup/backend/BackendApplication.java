package az.azcup.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Tətbiqin giriş nöqtəsi (entry point). @SpringBootApplication annotasiyası
// üç şeyi birləşdirir: avtomatik konfiqurasiya (məs. datasource-u application.yml-dən
// oxumaq), komponent-skan (bu paket və alt-paketlərdəki bütün @Service/@Component/
// @RestController siniflərini tapıb qeydiyyatdan keçirmək) və Spring konfiqurasiyası.
@SpringBootApplication
public class BackendApplication {

    // Tətbiq buradan başlayır: daxili Tomcat serverini qaldırır, verilənlər bazasına
    // qoşulur, bütün bean-ları yaradır və CommandLineRunner-ləri (məs.
    // AdminBootstrapRunner, SeedLoader) işə salır.
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
