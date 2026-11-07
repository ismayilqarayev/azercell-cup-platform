package az.azcup.backend.seed;

import az.azcup.backend.dto.quiz.QuizUpsertRequest;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import az.azcup.backend.repository.QuizRepository;
import az.azcup.backend.repository.UserRepository;
import az.azcup.backend.service.QuizAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Layihə ilə gələn hazır testləri (resources/seed/dim-python-tests.json: DİM informatika üçün orijinal Python testləri) bazaya yazır.
 * Fayl tools/dim-tests/build.py ilə yaradılır: hər cavab açarı sualın kodunu icra edərək hesablanıb.
 *
 * Prinsip SeedLoader ilə eynidir ("yoxdursa yarat, varsa toxunma"): hər testin sabit açarı (seedKey) var və bu açarlı test bazada
 * artıq mövcuddursa o atlanır, yəni admin/müəllimin etdiyi redaktələr yenidən başlatmada pozulmur. Testlər admin sahibliyində, DƏRC
 * OLUNMUŞ yaranır (admin dərcdən çıxara bilər). Hazır test silinə bilmir (bax: QuizAdminService.delete), çünki silinsə növbəti
 * başlanğıcda yenidən yaranardı.
 *
 * Admin hesabı AdminBootstrapRunner-də (@Order(1)) yaranır; o hələ yoxdursa bu yükləyici heç nə etmir və növbəti başlanğıcda təkrar yoxlayır.
 * Söndürmək üçün: SEED_QUIZZES=false (app.seed.quizzes.enabled).
 */
@Component
public class QuizSeedLoader implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(QuizSeedLoader.class);
    static final String RESOURCE = "seed/dim-python-tests.json";

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuizAdminService quizAdminService;
    private final ObjectMapper objectMapper;

    @Value("${app.seed.quizzes.enabled:true}")
    private boolean enabled;

    public QuizSeedLoader(UserRepository userRepository, QuizRepository quizRepository,
                          QuizAdminService quizAdminService, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.quizAdminService = quizAdminService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws IOException {
        if (!enabled) {
            return;
        }
        User owner = userRepository.findFirstByRoleOrderByIdAsc(Role.ADMIN).orElse(null);
        if (owner == null) {
            log.warn("Hazır testlər yüklənmədi: hələ admin hesabı yoxdur (növbəti başlanğıcda təkrar yoxlanacaq)");
            return;
        }
        JsonNode root;
        try (InputStream input = new ClassPathResource(RESOURCE).getInputStream()) {
            root = objectMapper.readTree(input);
        }
        // Şagird siyahısı "ən yenisi birinci" sıralanır; testlər tərsinə yaradılır ki, siyahıda fayldakı sıra (Python 1 ... Python 10, sınaq) görünsün.
        List<JsonNode> nodes = new ArrayList<>();
        root.forEach(nodes::add);
        Collections.reverse(nodes);
        int created = 0;
        for (JsonNode node : nodes) {
            JsonNode keyNode = node.get("seedKey");
            String key = keyNode == null ? "" : keyNode.asString();
            if (key.isBlank()) {
                log.error("Hazır test seedKey-siz buraxıldı: {}", node.get("title"));
                continue;
            }
            if (quizRepository.existsBySeedKey(key)) {
                continue;
            }
            // Hər test ayrıca tranzaksiyada yaranır: biri rədd edilsə, qalanları təsirlənmir və başlanğıc dayanmır.
            try {
                ObjectNode copy = ((ObjectNode) node).deepCopy();
                copy.remove("seedKey");
                QuizUpsertRequest parsed = objectMapper.treeToValue(copy, QuizUpsertRequest.class);
                QuizUpsertRequest published = new QuizUpsertRequest(
                    parsed.title(), parsed.description(), parsed.category(), parsed.timeLimitMinutes(), true, parsed.questions());
                quizAdminService.createSeeded(owner, key, published);
                created++;
            } catch (RuntimeException e) {
                log.error("Hazır test yaradıla bilmədi ({}): {}", key, e.getMessage());
            }
        }
        if (created > 0) {
            log.info("Seeded {} ready-made quizzes", created);
        }
    }
}
