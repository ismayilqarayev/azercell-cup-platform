package az.azcup.backend.controller;

import az.azcup.backend.dto.contest.ContestDetailDto;
import az.azcup.backend.dto.contest.ContestDto;
import az.azcup.backend.dto.contest.ContestSubmissionRequest;
import az.azcup.backend.dto.contest.ContestSubmissionResponse;
import az.azcup.backend.dto.contest.LeaderboardEntryDto;
import az.azcup.backend.security.UserPrincipal;
import az.azcup.backend.service.ContestService;
import az.azcup.backend.service.ContestSubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// "/api/contests" prefiksi altında — SecurityConfig-in mövcud
// "/api/**" -> authenticated() qaydası bu yolu artıq əhatə edir (ƏLAVƏ
// konfiqurasiyaya ehtiyac yoxdur); "yalnız STUDENT qoşula/göndərə bilər"
// qaydası isə burada YOX, ContestService/ContestSubmissionService-in
// daxilində tətbiq olunur (bax: onlardakı rol yoxlamaları) — eyni
// ProblemService/SubmissionService-də olduğu kimi.
@RestController
@RequestMapping("/api/contests")
public class ContestController {

    // Yarışlara baxmaq/qoşulmaq/reytinq üçün.
    private final ContestService contestService;
    // Kod göndərmək/tarixçəyə baxmaq üçün.
    private final ContestSubmissionService contestSubmissionService;

    // Spring tərəfindən inject olunan servisləri sahələrə təyin edir.
    public ContestController(ContestService contestService, ContestSubmissionService contestSubmissionService) {
        this.contestService = contestService;
        this.contestSubmissionService = contestSubmissionService;
    }

    // Bütün yarışların qısa siyahısı.
    @GetMapping
    public List<ContestDto> list() {
        return contestService.list();
    }

    // Tək bir yarışın tam detalları (status, qoşulma vəziyyəti, məsələlər).
    @GetMapping("/{id}")
    public ContestDetailDto get(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return contestService.getDetail(id, principal.getUser());
    }

    // Şagirdi yarışa qoşur.
    @PostMapping("/{id}/join")
    public ResponseEntity<Void> join(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        contestService.join(id, principal.getUser());
        return ResponseEntity.noContent().build();
    }

    // Bir yarışın reytinq cədvəli.
    @GetMapping("/{id}/leaderboard")
    public List<LeaderboardEntryDto> leaderboard(@PathVariable Long id) {
        return contestService.leaderboard(id);
    }

    // Bir yarış məsələsinə kod göndərir, yoxlatdırır və nəticəni qaytarır.
    @PostMapping("/problems/{contestProblemId}/submissions")
    public ContestSubmissionResponse submit(
        @PathVariable Long contestProblemId,
        @Valid @RequestBody ContestSubmissionRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return contestSubmissionService.submit(contestProblemId, principal.getUser(), request.getSourceCode());
    }

    // Bu istifadəçinin bu yarış məsələsinə etdiyi bütün cəhdlərin tarixçəsi.
    @GetMapping("/problems/{contestProblemId}/submissions")
    public List<ContestSubmissionResponse> history(
        @PathVariable Long contestProblemId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return contestSubmissionService.history(contestProblemId, principal.getUser());
    }
}
