package az.azcup.backend.seed;

import az.azcup.backend.service.QuizAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Bir buraxılışda (2026-11-07) tətbiq başlayanda bazaya 11 hazır Python testi yazılırdı. Onlar ləğv olunub: sual faylı və yükləyici
 * repodan silinib (repo açıqdır, cavab açarları hamıya görünürdü). Bu sinif həmin testləri CANLI BAZADAN da silir: seedKey-i olan
 * hər testi cəhdləri və cavabları ilə birlikdə. Əl ilə yaradılan testlərə toxunmur.
 *
 * Təkrar işə düşəndə silinəcək heç nə qalmadığı üçün heç nə etmir. Canlı serverdə bir dəfə işlədikdən sonra bu sinif,
 * QuizAdminService.deleteSeededQuizzes və Quiz.seedKey sahəsi koddan çıxarıla bilər.
 */
@Component
public class QuizSeedCleanup implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(QuizSeedCleanup.class);

    private final QuizAdminService quizAdminService;

    public QuizSeedCleanup(QuizAdminService quizAdminService) {
        this.quizAdminService = quizAdminService;
    }

    @Override
    public void run(String... args) {
        // Təmizləmə uğursuz olsa belə tətbiq işə düşməlidir: xəta yalnız qeyd olunur, növbəti başlanğıcda yenidən cəhd edilir.
        try {
            int removed = quizAdminService.deleteSeededQuizzes();
            if (removed > 0) {
                log.info("Removed {} ready-made quizzes (with their attempts)", removed);
            }
        } catch (RuntimeException e) {
            log.error("Ready-made quizzes could not be removed: {}", e.getMessage());
        }
    }
}
