package az.azcup.backend.controller;

import az.azcup.backend.dto.ProblemDetailDto;
import az.azcup.backend.dto.ProblemSummaryDto;
import az.azcup.backend.security.UserPrincipal;
import az.azcup.backend.service.ProblemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Problemlərin oxunması (görüntülənməsi) üçün — yaratma/redaktə AdminController-dədir.
@RestController
public class ProblemController {

    // Problem sorğularının faktiki icraçısı.
    private final ProblemService problemService;

    // Spring tərəfindən inject olunan ProblemService-i sahəyə təyin edir.
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    // Bir mövzunun (məs. "week1") bütün problemlərinin QISA siyahısı.
    // principal.getUser() ötürülür ki, hər problem üçün "solved" (bu
    // istifadəçi artıq həll edib mi) sahəsi düzgün hesablansın.
    @GetMapping("/api/topics/{slug}/problems")
    public List<ProblemSummaryDto> listByTopic(
        @PathVariable String slug,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return problemService.listByTopicSlug(slug, principal.getUser());
    }

    // Bir problemin TAM detalları (şərt, giriş/çıxış spesifikasiyası, nümunələr).
    @GetMapping("/api/problems/{id}")
    public ProblemDetailDto getDetail(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return problemService.getDetail(id, principal.getUser());
    }
}
