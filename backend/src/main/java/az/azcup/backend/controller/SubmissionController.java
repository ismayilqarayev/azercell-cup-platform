package az.azcup.backend.controller;

import az.azcup.backend.dto.ProgressDto;
import az.azcup.backend.dto.StudentAssignmentDto;
import az.azcup.backend.dto.SubmissionRequest;
import az.azcup.backend.dto.SubmissionResponse;
import az.azcup.backend.security.UserPrincipal;
import az.azcup.backend.service.AssignmentService;
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

    // Təqdimatlarla bağlı bütün əməliyyatların faktiki icraçısı.
    private final SubmissionService submissionService;
    // Şagirdin öz tapşırıqlarını (bax: /api/me/assignments) gətirmək üçün.
    private final AssignmentService assignmentService;

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public SubmissionController(SubmissionService submissionService, AssignmentService assignmentService) {
        this.submissionService = submissionService;
        this.assignmentService = assignmentService;
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

    // Şagirdin ÜZVÜ olduğu bütün qruplardakı tapşırıqları, öz irəliləyişi ilə
    // birlikdə qaytarır — "Tapşırıqlarım" ekranı üçün.
    @GetMapping("/api/me/assignments")
    public List<StudentAssignmentDto> myAssignments(@AuthenticationPrincipal UserPrincipal principal) {
        return assignmentService.listForStudent(principal.getUser());
    }
}
