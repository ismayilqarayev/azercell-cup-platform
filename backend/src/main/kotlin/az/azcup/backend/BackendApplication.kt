package az.azcup.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// Tətbiqin giriş nöqtəsi (entry point). @SpringBootApplication annotasiyası
// üç şeyi birləşdirir: avtomatik konfiqurasiya (məs. datasource-u application.yml-dən
// oxumaq), komponent-skan (bu paket və alt-paketlərdəki bütün @Service/@Component/
// @RestController siniflərini tapıb qeydiyyatdan keçirmək) və Spring konfiqurasiyası.
@SpringBootApplication
class BackendApplication

// Tətbiq buradan başlayır: daxili Tomcat serverini qaldırır, verilənlər bazasına
// qoşulur, bütün bean-ları yaradır və CommandLineRunner-ləri (məs.
// AdminBootstrapRunner, SeedLoader) işə salır.
fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}
