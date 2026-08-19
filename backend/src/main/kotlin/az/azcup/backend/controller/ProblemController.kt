package az.azcup.backend.controller

import az.azcup.backend.dto.ProblemDetailDto
import az.azcup.backend.dto.ProblemSummaryDto
import az.azcup.backend.security.UserPrincipal
import az.azcup.backend.service.ProblemService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

// Problemlərin oxunması (görüntülənməsi) üçün — yaratma/redaktə AdminController-dədir.
@RestController
class ProblemController(private val problemService: ProblemService) {

    // Bir mövzunun (məs. "week1") bütün problemlərinin QISA siyahısı.
    // principal.user ötürülür ki, hər problem üçün "solved" (bu istifadəçi
    // artıq həll edib mi) sahəsi düzgün hesablansın.
    @GetMapping("/api/topics/{slug}/problems")
    fun listByTopic(
        @PathVariable slug: String,
        @AuthenticationPrincipal principal: UserPrincipal
    ): List<ProblemSummaryDto> = problemService.listByTopicSlug(slug, principal.user)

    // Bir problemin TAM detalları (şərt, giriş/çıxış spesifikasiyası, nümunələr).
    @GetMapping("/api/problems/{id}")
    fun getDetail(@PathVariable id: Long, @AuthenticationPrincipal principal: UserPrincipal): ProblemDetailDto =
        problemService.getDetail(id, principal.user)
}
