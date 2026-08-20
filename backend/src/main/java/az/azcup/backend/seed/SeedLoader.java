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
import org.springframework.transaction.annotation.Transactional;
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
 */
@Component
public class SeedLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedLoader.class);

    private final TopicRepository topicRepository;
    private final ProblemRepository problemRepository;
    private final ObjectMapper objectMapper;

    public SeedLoader(TopicRepository topicRepository, ProblemRepository problemRepository, ObjectMapper objectMapper) {
        this.topicRepository = topicRepository;
        this.problemRepository = problemRepository;
        this.objectMapper = objectMapper;
    }

    // AdminBootstrapRunner ilə eyni prinsip: Spring Boot tətbiq tam
    // yükləndikdən sonra bunu AVTOMATİK çağırır.
    @Transactional
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

        // Əvvəlcə BÜTÜN mövzular yaradılır və slug->Topic map-ində saxlanılır —
        // çünki hər problem öz mövzusuna JPA əlaqəsi (topic = bySlug[...]) ilə
        // bağlanmalıdır, bu da mövzunun artıq (ID almış, bazaya yazılmış)
        // mövcud olmasını tələb edir.
        Map<String, Topic> bySlug = new HashMap<>();
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

        // Sonra bütün problemlər yaradılır, hər biri öz mövzusuna bağlanaraq.
        List<Problem> problems = new ArrayList<>();
        for (SeedProblem sp : data.getProblems()) {
            Problem p = new Problem();
            p.setTopic(bySlug.get(sp.getTopicSlug()));
            p.setOrderIndex(sp.getOrderIndex());
            p.setSubgroupLabel(sp.getSubgroupLabel());
            p.setTitle(sp.getTitle());
            // JSON-dakı mətn ("easy"/"mid"/"hard") Difficulty enum-una çevrilir.
            p.setDifficulty(Difficulty.valueOf(sp.getDifficulty().toUpperCase(Locale.ROOT)));
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
        // saveAll() — hər problemi ayrı-ayrı save() ilə (680 dəfə ayrı INSERT)
        // yazmaq əvəzinə, Hibernate-ə toplu (batch) yazma imkanı verir.
        problemRepository.saveAll(problems);

        log.info("Seeded {} topics and {} problems", data.getTopics().size(), problems.size());
    }
}
