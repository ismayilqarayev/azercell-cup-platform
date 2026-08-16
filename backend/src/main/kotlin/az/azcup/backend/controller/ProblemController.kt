package az.azcup.backend.controller

import az.azcup.backend.dto.ProblemDetailDto
import az.azcup.backend.dto.ProblemSummaryDto
import az.azcup.backend.security.UserPrincipal
import az.azcup.backend.service.ProblemService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ProblemController(private val problemService: ProblemService) {

    @GetMapping("/api/topics/{slug}/problems")
    fun listByTopic(
        @PathVariable slug: String,
        @AuthenticationPrincipal principal: UserPrincipal
    ): List<ProblemSummaryDto> = problemService.listByTopicSlug(slug, principal.user)

    @GetMapping("/api/problems/{id}")
    fun getDetail(@PathVariable id: Long, @AuthenticationPrincipal principal: UserPrincipal): ProblemDetailDto =
        problemService.getDetail(id, principal.user)
}
