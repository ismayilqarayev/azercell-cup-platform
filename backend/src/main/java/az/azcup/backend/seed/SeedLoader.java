package az.azcup.backend.seed;

import az.azcup.backend.entity.Difficulty;
import az.azcup.backend.entity.Problem;
import az.azcup.backend.entity.Topic;
import az.azcup.backend.repository.ProblemRepository;
import az.azcup.backend.repository.TopicRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Orijinal statik index.html-dən çıxarılmış (bax: tools/extract-seed-data.js)
 * 16 mövzu / 680 problemdən ibarət dataseti verilənlər bazasına YÜKLƏYİR —
 * amma bunu YALNIZ BİR DƏFƏ edir: topic cədvəli artıq boş deyilsə, heç nə
 * etmədən çıxır. Beləliklə tətbiqi neçə dəfə yenidən başlatsan da, məlumat
 * nə təkrarlanır, nə də sıfırlanır (admin panelindən edilmiş dəyişikliklər qorunur).
 *
 * DİQQƏT: bütün 800+ problemi TƏK bir (@Transactional) əməliyyatda yazmaq
 * uzaq (məs. Neon kimi WAN üzərindən qoşulan) verilənlər bazalarında bir neçə
 * dəqiqə çəkən NƏHƏNG bir tranzaksiyaya səbəb olurdu — bu, sıra (sequence)
 * dəyərlərini irəli aparsa da, sonda commit olunmadan sükutla geri (rollback)
 * dönürdü (uzun tranzaksiyanı proxy qatının kəsməsi ehtimal olunur). Ona görə
 * problemlər KİÇİK PARTİYALARLA (hər biri öz qısa tranzaksiyasında) yazılır.
 */
@Component
public class SeedLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedLoader.class);

    // Hər partiyada neçə problem yazılacaq — kiçik saxlanılır ki, hər
    // tranzaksiya bir neçə saniyədən çox çəkməsin (uzaq DB-lərdə də).
    private static final int BATCH_SIZE = 50;

    // Mövzuları bazaya yazmaq/oxumaq üçün.
    private final TopicRepository topicRepository;
    // Problemləri bazaya yazmaq/oxumaq üçün.
    private final ProblemRepository problemRepository;
    // seed-data.json-u Java obyektlərinə (SeedData) çevirmək üçün Jackson kitabxanasının əsas sinfi.
    private final ObjectMapper objectMapper;
    // Hər partiyanı ayrıca, qısa bir tranzaksiyada commit etmək üçün —
    // @Transactional annotasiyası əvəzinə proqramatik istifadə olunur ki,
    // eyni sinif daxilində (self-invocation) belə düzgün işləsin.
    private final TransactionTemplate transactionTemplate;

    // Spring tərəfindən inject olunan asılılıqları (repository-lər,
    // ObjectMapper və tranzaksiya meneceri) sahələrə təyin edir.
    public SeedLoader(TopicRepository topicRepository, ProblemRepository problemRepository,
                       ObjectMapper objectMapper, PlatformTransactionManager transactionManager) {
        this.topicRepository = topicRepository;
        this.problemRepository = problemRepository;
        this.objectMapper = objectMapper;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    // AdminBootstrapRunner ilə eyni prinsip: Spring Boot tətbiq tam
    // yükləndikdən sonra bunu AVTOMATİK çağırır.
    @Override
    public void run(String... args) throws IOException {
        // "Boşdursa doldur, doludursa toxunma" — seeding-in TƏKRARLANMAMASINI
        // təmin edən əsas yoxlama.
        if (topicRepository.count() > 0) {
            log.info("Seed skipped — topics table already populated");
            return;
        }

        // seed-data.json resurs (classpath) daxilindən oxunur — build zamanı
        // JAR-ın içinə paketlənir, ona görə fayl sistemində ayrıca yol axtarmağa
        // ehtiyac yoxdur.
        SeedData data;
        try (InputStream input = new ClassPathResource("seed/seed-data.json").getInputStream()) {
            data = objectMapper.readValue(input, SeedData.class);
        }

        // Mövzular sayca az olduğu üçün (16 ədəd) tək bir qısa tranzaksiyada
        // yazılır. Nəticədə hər mövzunun artıq ID-si olur, bu da problemləri
        // ona bağlamaq üçün lazımdır.
        Map<String, Topic> bySlug = new HashMap<>();
        transactionTemplate.executeWithoutResult(status -> {
            for (SeedTopic st : data.getTopics()) {
                Topic topic = new Topic();
                topic.setSlug(st.getSlug());
                topic.setOrderIndex(st.getOrderIndex());
                topic.setTitle(st.getTitle());
                topic.setMonthTag(st.getMonthTag());
                topic.setDescription(st.getDescription());
                topic.setPublished(st.isPublished());
                topicRepository.save(topic);
                bySlug.put(st.getSlug(), topic);
            }
        });

        // Bütün problem obyektlərini əvvəlcə yaddaşda (DB-yə hələ toxunmadan) qururuq.
        List<Problem> problems = new ArrayList<>();
        for (SeedProblem sp : data.getProblems()) {
            Problem p = new Problem();
            p.setTopic(bySlug.get(sp.getTopicSlug()));
            p.setOrderIndex(sp.getOrderIndex());
            p.setSubgroupLabel(sp.getSubgroupLabel());
            p.setTitle(sp.getTitle());
            // JSON-dakı mətn ("easy"/"mid"/"hard") Difficulty enum-una çevrilir.
            p.setDifficulty(Difficulty.valueOf(sp.getDifficulty().toUpperCase(Locale.ROOT)));
            // JSON-da "tags" sahəsi olmaya bilər (null) — bu halda boş siyahı
            // istifadə olunur ki, Problem.tags heç vaxt null olmasın.
            List<String> tags;
            if (sp.getTags() != null) {
                tags = new ArrayList<>(sp.getTags());
            } else {
                tags = new ArrayList<>();
            }
            p.setTags(tags);
            p.setStatement(sp.getStatement());
            p.setInputSpec(sp.getInputSpec());
            p.setOutputSpec(sp.getOutputSpec());
            // exampleInput də JSON-da olmaya bilər — null yerinə boş sətir istifadə olunur.
            String exampleInput;
            if (sp.getExampleInput() != null) {
                exampleInput = sp.getExampleInput();
            } else {
                exampleInput = "";
            }
            p.setExampleInput(exampleInput);
            p.setExampleOutput(sp.getExampleOutput());
            p.setApproach(sp.getApproach());
            p.setReferenceSolution(sp.getReferenceSolution());
            problems.add(p);
        }

        // Problemləri BATCH_SIZE-lıq partiyalarla, hər partiyanı ÖZ qısa
        // tranzaksiyasında yazırıq — bax: sinif-üstü şərh (uzun tranzaksiya problemi).
        int total = problems.size();
        for (int start = 0; start < total; start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE, total);
            List<Problem> batch = problems.subList(start, end);
            transactionTemplate.executeWithoutResult(status -> problemRepository.saveAll(batch));
        }

        log.info("Seeded {} topics and {} problems", data.getTopics().size(), problems.size());
    }
}
