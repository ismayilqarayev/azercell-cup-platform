package az.azcup.backend.service

import az.azcup.backend.dto.TopicDto
import az.azcup.backend.entity.Topic
import az.azcup.backend.entity.User
import az.azcup.backend.exception.NotFoundException
import az.azcup.backend.repository.ProblemRepository
import az.azcup.backend.repository.SubmissionRepository
import az.azcup.backend.repository.TopicRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TopicService(
    private val topicRepository: TopicRepository,
    private val problemRepository: ProblemRepository,
    private val submissionRepository: SubmissionRepository
) {

    @Transactional(readOnly = true)
    fun listTopics(user: User): List<TopicDto> {
        val topics = topicRepository.findAllByOrderByOrderIndexAsc()
        val solvedByTopicId = submissionRepository.solvedCountsByTopicForUser(user)
            .associate { it.topicId to it.solvedCount }
        return topics.map { t ->
            TopicDto(
                t.id,
                t.slug,
                t.orderIndex,
                t.title,
                t.monthTag,
                t.description,
                t.published,
                problemRepository.countByTopic(t),
                solvedByTopicId.getOrDefault(t.id!!, 0L)
            )
        }
    }

    @Transactional(readOnly = true)
    fun getBySlug(slug: String): Topic =
        topicRepository.findBySlug(slug) ?: throw NotFoundException("Mövzu tapılmadı: $slug")
}
