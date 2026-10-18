package az.azcup.backend.config;

import java.time.Duration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Şrift fayllarına (frontend UI qatı) uzunmüddətli keş verir. Spring Security bütün cavablara
 * "Cache-Control: no-store" yazır; bu, hər iframe səhifəsinin şriftləri hər açılışda yenidən endirməsinə səbəb olurdu.
 * Başlıq burada resurs idarəçisi tərəfindən əvvəlcədən təyin olunduğu üçün Spring Security onu üzərinə yazmır.
 * Yalnız /assets/fonts/** əhatə olunur: index.html və API cavabları əvvəlki kimi keşlənmir.
 */
@Configuration
public class StaticAssetsConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/fonts/**")
                .addResourceLocations("classpath:/static/assets/fonts/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic());
    }
}
