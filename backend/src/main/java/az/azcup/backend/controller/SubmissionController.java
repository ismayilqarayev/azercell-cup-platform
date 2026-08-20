package az.azcup.backend.controller;

import az.azcup.backend.dto.ProgressDto;
import az.azcup.backend.dto.SubmissionRequest;
import az.azcup.backend.dto.SubmissionResponse;
import az.azcup.backend.security.UserPrincipal;
import az.azcup.backend.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Şagirdin kodunu göndərməsi, tarixçəsinə baxması və ümumi irəliləyişini
// görməsi üçün endpoint-lər — platformanın əsas "judge" axını.
@RestController
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    // Kodu compile+icra etdirir, nəticəni bazaya yazır və eyni zamanda
    // çağırana qaytarır (bax: SubmissionService.submit).
    @PostMapping("/api/problems/{id}/submissions")
    public SubmissionResponse submit(
        @PathVariable Long id,
        @Valid @RequestBody SubmissionRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return submissionService.submit(id, principal.getUser(), request.getSourceCode());
    }

    // Bu istifadəçinin bu problemə etdiyi bütün cəhdlərin tarixçəsi
    // ("Tarixçə" düyməsi bunu çağırır).
    @GetMapping("/api/problems/{id}/submissions")
    public List<SubmissionResponse> history(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return submissionService.history(id, principal.getUser());
    }

    // Hər mövzu üzrə "neçə problem, onlardan neçəsi həll edilib" statistikası
    // — irəliləyiş zolaqları üçün.
    @GetMapping("/api/me/progress")
    public List<ProgressDto> progress(@AuthenticationPrincipal UserPrincipal principal) {
        return submissionService.progress(principal.getUser());
    }
}
