package az.azcup.backend.service

import az.azcup.backend.dto.ProgressDto
import az.azcup.backend.dto.SubmissionResponse
import az.azcup.backend.entity.Role
import az.azcup.backend.entity.Submission
import az.azcup.backend.entity.User
import az.azcup.backend.judge.JudgeService
import az.azcup.backend.repository.ProblemRepository
import az.azcup.backend.repository.SubmissionRepository
import az.azcup.backend.repository.TopicRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// Şagirdin kod göndərməsini idarə edir: yoxlatdırır (JudgeService), nəticəni
// bazaya yazır və irəliləyiş statistikasını hesablayır.
@Service
class SubmissionService(
    private val submissionRepository: SubmissionRepository,
    private val problemService: ProblemService,
    private val topicRepository: TopicRepository,
    private val problemRepository: ProblemRepository,
    private val judgeService: JudgeService
) {

    // Kodu qəbul edir, compile+icra etdirir (JudgeService.judge) və nəticəni
    // (uğurlu da olsa, uğursuz da) DAİMİ olaraq bazaya yazır — beləliklə
    // tarixçə (history) heç vaxt boş qalmır, hətta kompilyasiya xətası olsa belə.
    @Transactional
    fun submit(problemId: Long, user: User, sourceCode: String): SubmissionResponse {
        // getEntity() daxildə "bu problem hələ açıq olmayan mövzudadırsa,
        // STUDENT ona kod göndərə bilməz" qaydasını da tətbiq edir.
        val problem = problemService.getEntity(problemId, user)
        val result = judgeService.judge(sourceCode, problem)

        val submission = Submission(
            user = user,
            problem = problem,
            sourceCode = sourceCode,
            status = result.status,
            stdout = result.stdout,
            stderr = result.stderr,
            executionTimeMs = result.executionTimeMs
        )
        submissionRepository.save(submission)
        return toResponse(submission)
    }

    // Bu istifadəçinin bu problemə etdiyi bütün cəhdlərin tarixçəsi.
    @Transactional(readOnly = true)
    fun history(problemId: Long, user: User): List<SubmissionResponse> {
        val problem = problemService.getEntity(problemId, user)
        return submissionRepository.findByUserAndProblemOrderBySubmittedAtDesc(user, problem)
            .map { toResponse(it) }
    }

    // Hər mövzu üzrə "cəmi neçə problem var, onlardan neçəsini bu istifadəçi
    // həll edib" statistikası — TopicService.listTopics-dəki eyni məntiqin
    // (STUDENT üçün yalnız dərc olunmuş mövzuları filtrləmə) bir daha
    // istifadəsi, sadəcə fərqli DTO (ProgressDto) formasında.
    @Transactional(readOnly = true)
    fun progress(user: User): List<ProgressDto> {
        val topics = topicRepository.findAllByOrderByOrderIndexAsc()
            .filter { user.role != Role.STUDENT || it.published }
        val solvedByTopicId = submissionRepository.solvedCountsByTopicForUser(user)
            .associate { it.topicId to it.solvedCount }
        return topics.map { t ->
            ProgressDto(
                t.slug,
                t.title,
                problemRepository.countByTopic(t),
                solvedByTopicId.getOrDefault(t.id!!, 0L)
            )
        }
    }

    private fun toResponse(s: Submission): SubmissionResponse =
        SubmissionResponse(
            s.id, s.problem?.id, s.sourceCode, s.status,
            s.stdout, s.stderr, s.executionTimeMs, s.submittedAt
        )
}
