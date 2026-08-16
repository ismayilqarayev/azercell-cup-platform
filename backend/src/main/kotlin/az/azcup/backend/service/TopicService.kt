package az.azcup.backend.service

import az.azcup.backend.dto.TopicDto
import az.azcup.backend.entity.Role
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

    // STUDENT rolu üçün yalnız müəllim/admin tərəfindən "açıq" elan olunmuş
    // (published=true) mövzular göstərilir — TEACHER və ADMIN isə hələ açıq
    // olmayanları da görür ki, dərc etməzdən əvvəl nəzərdən keçirə bilsin.
    @Transactional(readOnly = true)
    fun listTopics(user: User): List<TopicDto> {
        val topics = topicRepository.findAllByOrderByOrderIndexAsc()
            .filter { user.role != Role.STUDENT || it.published }
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

    // Bir STUDENT-in hələ açıq elan olunmamış mövzunu slug-unu bilərəkdən
    // sınayaraq (URL-i "təxmin edərək") görməsinin qarşısını almaq üçün, bu
    // yoxlama TopicController-in özündə deyil, birbaşa burada aparılır —
    // beləliklə bu metoddan keçən HƏR yol (mövzu siyahısı, məsələ siyahısı,
    // məsələ təfərrüatı) eyni qaydaya tabe olur. Mövcud olmayan mövzu ilə
    // eyni NotFoundException atılır ki, "mövzu yoxdur" ilə "mövzu hələ açıq
    // deyil" halları bir-birindən fərqləndirilə bilinməsin.
    @Transactional(readOnly = true)
    fun getBySlug(slug: String, user: User): Topic {
        val topic = topicRepository.findBySlug(slug) ?: throw NotFoundException("Mövzu tapılmadı: $slug")
        if (user.role == Role.STUDENT && !topic.published) {
            throw NotFoundException("Mövzu tapılmadı: $slug")
        }
        return topic
    }
}
