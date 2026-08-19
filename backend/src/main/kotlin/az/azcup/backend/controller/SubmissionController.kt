package az.azcup.backend.controller

import az.azcup.backend.dto.ProgressDto
import az.azcup.backend.dto.SubmissionRequest
import az.azcup.backend.dto.SubmissionResponse
import az.azcup.backend.security.UserPrincipal
import az.azcup.backend.service.SubmissionService
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

// Şagirdin kodunu göndərməsi, tarixçəsinə baxması və ümumi irəliləyişini
// görməsi üçün endpoint-lər — platformanın əsas "judge" axını.
@RestController
class SubmissionController(private val submissionService: SubmissionService) {

    // Kodu compile+icra etdirir, nəticəni bazaya yazır və eyni zamanda
    // çağırana qaytarır (bax: SubmissionService.submit).
    @PostMapping("/api/problems/{id}/submissions")
    fun submit(
        @PathVariable id: Long,
        @Valid @RequestBody request: SubmissionRequest,
        @AuthenticationPrincipal principal: UserPrincipal
    ): SubmissionResponse = submissionService.submit(id, principal.user, request.sourceCode)

    // Bu istifadəçinin bu problemə etdiyi bütün cəhdlərin tarixçəsi
    // ("Tarixçə" düyməsi bunu çağırır).
    @GetMapping("/api/problems/{id}/submissions")
    fun history(@PathVariable id: Long, @AuthenticationPrincipal principal: UserPrincipal): List<SubmissionResponse> =
        submissionService.history(id, principal.user)

    // Hər mövzu üzrə "neçə problem, onlardan neçəsi həll edilib" statistikası
    // — irəliləyiş zolaqları üçün.
    @GetMapping("/api/me/progress")
    fun progress(@AuthenticationPrincipal principal: UserPrincipal): List<ProgressDto> =
        submissionService.progress(principal.user)
}
