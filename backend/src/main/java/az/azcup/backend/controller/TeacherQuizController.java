package az.azcup.backend.controller;

import az.azcup.backend.dto.quiz.QuizManageDetailDto;
import az.azcup.backend.dto.quiz.QuizManageSummaryDto;
import az.azcup.backend.dto.quiz.QuizResultsDto;
import az.azcup.backend.dto.quiz.QuizUpsertRequest;
import az.azcup.backend.security.UserPrincipal;
import az.azcup.backend.service.QuizAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Testlərin idarəsi. "/api/teacher/**" SecurityConfig-də TEACHER və ADMIN-ə açıqdır; sahiblik yoxlaması QuizAdminService-dədir:
// müəllim yalnız öz testini, admin hamısını idarə edir.
@RestController
@RequestMapping("/api/teacher/quizzes")
public class TeacherQuizController {

    private final QuizAdminService quizAdminService;

    public TeacherQuizController(QuizAdminService quizAdminService) {
        this.quizAdminService = quizAdminService;
    }

    @GetMapping
    public List<QuizManageSummaryDto> list(@AuthenticationPrincipal UserPrincipal principal) {
        return quizAdminService.list(principal.getUser());
    }

    @PostMapping
    public ResponseEntity<QuizManageDetailDto> create(
        @Valid @RequestBody QuizUpsertRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizAdminService.create(principal.getUser(), request));
    }

    // Redaktor üçün tam hal (düzgün cavablarla).
    @GetMapping("/{id}")
    public QuizManageDetailDto get(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return quizAdminService.get(id, principal.getUser());
    }

    @PutMapping("/{id}")
    public QuizManageDetailDto update(
        @PathVariable Long id,
        @Valid @RequestBody QuizUpsertRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return quizAdminService.update(id, principal.getUser(), request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        quizAdminService.delete(id, principal.getUser());
        return ResponseEntity.noContent().build();
    }

    // Şagirdlər üzrə nəticələr və sual statistikası (müəllim üçün yalnız öz qruplarının şagirdləri).
    @GetMapping("/{id}/results")
    public QuizResultsDto results(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return quizAdminService.results(id, principal.getUser());
    }
}
