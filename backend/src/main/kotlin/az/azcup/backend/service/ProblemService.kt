package az.azcup.backend.service

import az.azcup.backend.dto.ProblemDetailDto
import az.azcup.backend.dto.ProblemSummaryDto
import az.azcup.backend.entity.Problem
import az.azcup.backend.entity.Role
import az.azcup.backend.entity.SubmissionStatus
import az.azcup.backend.entity.User
import az.azcup.backend.exception.NotFoundException
import az.azcup.backend.repository.ProblemRepository
import az.azcup.backend.repository.SubmissionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

// Problemlərin OXUNMASI (siyahı və detal) ilə bağlı biznes-məntiq.
// Yaratma/redaktə AdminService-dədir, göndərmə/yoxlama SubmissionService-dədir.
@Service
class ProblemService(
    private val problemRepository: ProblemRepository,
    private val submissionRepository: SubmissionRepository,
    private val topicService: TopicService
) {

    // Mövzunun slug-una görə (məs. "week1") o mövzudakı bütün problemlərin
    // qısa siyahısını qaytarır. topicService.getBySlug() daxildə mövzunun
    // açıq (published) olub-olmadığını da yoxlayır — STUDENT hələ açılmamış
    // mövzunu bu yolla görə bilməz.
    @Transactional(readOnly = true)
    fun listByTopicSlug(slug: String, user: User): List<ProblemSummaryDto> {
        val topic = topicService.getBySlug(slug, user)
        val problems = problemRepository.findAllByTopicOrderByOrderIndexAsc(topic)
        // Bu istifadəçinin hansı problemləri artıq həll etdiyini TƏK bir
        // sorğu ilə əldə edib Set-ə çeviririk ki, aşağıdakı map() içində hər
        // problem üçün ayrıca bazaya getmək əvəzinə O(1) yoxlama aparılsın.
        val solvedIds = submissionRepository.solvedProblemIdsForUserInTopic(user, topic).toSet()
        return problems.map { p ->
            ProblemSummaryDto(
                p.id, p.orderIndex, p.subgroupLabel, p.title,
                p.difficulty, p.tags, solvedIds.contains(p.id)
            )
        }
    }

    // Bir problemin tam detallarını (şərt, giriş/çıxış spesifikasiyası,
    // nümunələr) qaytarır.
    @Transactional(readOnly = true)
    fun getDetail(id: Long, user: User): ProblemDetailDto {
        val p = getEntity(id, user)
        val solved = submissionRepository.existsByUserAndProblemAndStatus(user, p, SubmissionStatus.ACCEPTED)
        return ProblemDetailDto(
            p.id, p.topic!!.slug, p.orderIndex, p.subgroupLabel, p.title,
            p.difficulty, p.tags, p.statement, p.inputSpec, p.outputSpec,
            p.exampleInput, p.exampleOutput, p.approach, solved
        )
    }

    // ID ilə xam Problem entity-sini tapır — həm getDetail(), həm də
    // SubmissionService (submit/history) buradan keçir, ona görə "bu məsələ
    // hələ açıq elan olunmayan bir mövzudadırsa, STUDENT ona nə baxa, nə də
    // kod göndərə bilməz" qaydası bir yerdə, mərkəzləşdirilmiş şəkildə
    // tətbiq olunur — problem ID-sini əl ilə "təxmin edərək" bu qadağanı
    // dolayı yolla keçmək mümkün olmasın deyə.
    @Transactional(readOnly = true)
    fun getEntity(id: Long, user: User): Problem {
        val p = problemRepository.findById(id).orElseThrow { NotFoundException("Məsələ tapılmadı: $id") }
        if (user.role == Role.STUDENT && p.topic?.published == false) {
            throw NotFoundException("Məsələ tapılmadı: $id")
        }
        return p
    }
}
