package az.azcup.backend.config;

import java.time.Duration;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Statik faylların keş qaydaları. Spring Security bütün cavablara "Cache-Control: no-store" yazır; başlıq burada resurs idarəçisi
 * tərəfindən əvvəlcədən təyin olunduğu üçün Spring Security onu üzərinə yazmır.
 *
 * - /assets/fonts/**: 30 günlük keş (şriftlər dəyişmir; əvvəl hər iframe səhifəsi onları hər açılışda yenidən endirirdi).
 * - /index.html (və "/" — o da index.html-ə yönləndirilir): "no-cache", yəni brauzer faylı saxlayır, amma HƏR açılışda serverdən
 *   soruşur (If-Modified-Since). Dəyişməyibsə server gövdəsiz 304 qaytarır. index.html 5 MB-dan böyükdür (sıxılmış ~1,4 MB) və əvvəl
 *   "no-store" üzündən hər açılışda və hər yenilənmədə tam endirilirdi. Yeni deploy faylın tarixini dəyişir, ona görə istifadəçilər
 *   köhnə versiyada qalmır. Faylda istifadəçiyə aid heç bir məlumat yoxdur (statik qabıqdır), ona görə saxlanması təhlükəsizdir.
 *
 * API cavabları əvvəlki kimi keşlənmir.
 */
@Configuration
public class StaticAssetsConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/fonts/**")
                .addResourceLocations("classpath:/static/assets/fonts/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(30)).cachePublic());
        registry.addResourceHandler("/index.html")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.noCache());
    }
}
