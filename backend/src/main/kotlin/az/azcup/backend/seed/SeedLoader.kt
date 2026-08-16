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
 * Loads the 16-topic / 680-problem dataset extracted from the original static
 * index.html (see tools/extract-seed-data.js) into the DB, once — only runs
 * when the topics table is empty, so restarts never duplicate or reset data.
 */
@Component
class SeedLoader(
    private val topicRepository: TopicRepository,
    private val problemRepository: ProblemRepository,
    private val objectMapper: ObjectMapper
) : CommandLineRunner {

    private val log = LoggerFactory.getLogger(SeedLoader::class.java)

    @Transactional
    override fun run(vararg args: String) {
        if (topicRepository.count() > 0) {
            log.info("Seed skipped — topics table already populated")
            return
        }

        val data: SeedData = ClassPathResource("seed/seed-data.json").inputStream.use { input ->
            objectMapper.readValue(input, SeedData::class.java)
        }

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

        val problems = data.problems.map { sp ->
            Problem(
                topic = bySlug[sp.topicSlug],
                orderIndex = sp.orderIndex,
                subgroupLabel = sp.subgroupLabel,
                title = sp.title,
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
        problemRepository.saveAll(problems)

        log.info("Seeded {} topics and {} problems", data.topics.size, problems.size)
    }
}
