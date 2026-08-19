package az.azcup.backend.seed

import az.azcup.backend.entity.Difficulty
import az.azcup.backend.entity.Problem
import az.azcup.backend.entity.Topic
import az.azcup.backend.repository.ProblemRepository
import az.azcup.backend.repository.TopicRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper

/**
 * Orijinal statik index.html-dən çıxarılmış (bax: tools/extract-seed-data.js)
 * 16 mövzu / 680 problemdən ibarət dataseti verilənlər bazasına YÜKLƏYİR —
 * amma bunu YALNIZ BİR DƏFƏ edir: topic cədvəli artıq boş deyilsə, heç nə
 * etmədən çıxır. Beləliklə tətbiqi neçə dəfə yenidən başlatsan da, məlumat
 * nə təkrarlanır, nə də sıfırlanır (admin panelindən edilmiş dəyişikliklər qorunur).
 */
@Component
class SeedLoader(
    private val topicRepository: TopicRepository,
    private val problemRepository: ProblemRepository,
    private val objectMapper: ObjectMapper
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(SeedLoader::class.java)

    // AdminBootstrapRunner ilə eyni prinsip: Spring Boot tətbiq tam
    // yükləndikdən sonra bunu AVTOMATİK çağırır.
    @Transactional
    override fun run(vararg args: String) {
        // "Boşdursa doldur, doludursa toxunma" — seeding-in TƏKRARLANMAMASINI
        // təmin edən əsas yoxlama.
        if (topicRepository.count() > 0) {
            log.info("Seed skipped — topics table already populated")
            return
        }

        // seed-data.json resurs (classpath) daxilindən oxunur — build zamanı
        // JAR-ın içinə paketlənir, ona görə fayl sistemində ayrıca yol axtarmağa
        // ehtiyac yoxdur.
        val data: SeedData = ClassPathResource("seed/seed-data.json").inputStream.use { input ->
            objectMapper.readValue(input, SeedData::class.java)
        }

        // Əvvəlcə BÜTÜN mövzular yaradılır və slug->Topic map-ində saxlanılır —
        // çünki hər problem öz mövzusuna JPA əlaqəsi (topic = bySlug[...]) ilə
        // bağlanmalıdır, bu da mövzunun artıq (ID almış, bazaya yazılmış)
        // mövcud olmasını tələb edir.
        val bySlug = HashMap<String, Topic>()
        for (st in data.topics) {
            val topic = Topic(
                slug = st.slug,
                orderIndex = st.orderIndex,
                title = st.title,
                monthTag = st.monthTag,
                description = st.description,
                published = st.published
            )
            topicRepository.save(topic)
            bySlug[st.slug] = topic
        }

        // Sonra bütün problemlər yaradılır, hər biri öz mövzusuna bağlanaraq.
        val problems = data.problems.map { sp ->
            Problem(
                topic = bySlug[sp.topicSlug],
                orderIndex = sp.orderIndex,
                subgroupLabel = sp.subgroupLabel,
                title = sp.title,
                // JSON-dakı mətn ("easy"/"mid"/"hard") Difficulty enum-una çevrilir.
                difficulty = Difficulty.valueOf(sp.difficulty.uppercase()),
                tags = (sp.tags ?: emptyList()).toMutableList(),
                statement = sp.statement,
                inputSpec = sp.inputSpec,
                outputSpec = sp.outputSpec,
                exampleInput = sp.exampleInput ?: "",
                exampleOutput = sp.exampleOutput,
                approach = sp.approach,
                referenceSolution = sp.referenceSolution
            )
        }
        // saveAll() — hər problemi ayrı-ayrı save() ilə (680 dəfə ayrı INSERT)
        // yazmaq əvəzinə, Hibernate-ə toplu (batch) yazma imkanı verir.
        problemRepository.saveAll(problems)

        log.info("Seeded {} topics and {} problems", data.topics.size, problems.size)
    }
}
