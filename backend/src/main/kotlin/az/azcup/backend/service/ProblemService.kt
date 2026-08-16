package az.azcup.backend.service

import az.azcup.backend.dto.ProblemDetailDto
import az.azcup.backend.dto.ProblemSummaryDto
import az.azcup.backend.entity.Problem
import az.azcup.backend.entity.SubmissionStatus
import az.azcup.backend.entity.User
import az.azcup.backend.exception.NotFoundException
import az.azcup.backend.repository.ProblemRepository
import az.azcup.backend.repository.SubmissionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProblemService(
    private val problemRepository: ProblemRepository,
    private val submissionRepository: SubmissionRepository,
    private val topicService: TopicService
) {

    @Transactional(readOnly = true)
    fun listByTopicSlug(slug: String, user: User): List<ProblemSummaryDto> {
        val topic = topicService.getBySlug(slug)
        val problems = problemRepository.findAllByTopicOrderByOrderIndexAsc(topic)
        val solvedIds = submissionRepository.solvedProblemIdsForUserInTopic(user, topic).toSet()
        return problems.map { p ->
            ProblemSummaryDto(
                p.id, p.orderIndex, p.subgroupLabel, p.title,
                p.difficulty, p.tags, solvedIds.contains(p.id)
            )
        }
    }

    @Transactional(readOnly = true)
    fun getDetail(id: Long, user: User): ProblemDetailDto {
        val p = getEntity(id)
        val solved = submissionRepository.existsByUserAndProblemAndStatus(user, p, SubmissionStatus.ACCEPTED)
        return ProblemDetailDto(
            p.id, p.topic!!.slug, p.orderIndex, p.subgroupLabel, p.title,
            p.difficulty, p.tags, p.statement, p.inputSpec, p.outputSpec,
            p.exampleInput, p.exampleOutput, p.approach, solved
        )
    }

    @Transactional(readOnly = true)
    fun getEntity(id: Long): Problem =
        problemRepository.findById(id).orElseThrow { NotFoundException("Məsələ tapılmadı: $id") }
}
