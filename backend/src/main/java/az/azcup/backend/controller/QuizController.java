package az.azcup.backend.controller;

import az.azcup.backend.dto.quiz.QuizAttemptStartDto;
import az.azcup.backend.dto.quiz.QuizResultDto;
import az.azcup.backend.dto.quiz.QuizSubmitRequest;
import az.azcup.backend.dto.quiz.QuizSummaryDto;
import az.azcup.backend.security.UserPrincipal;
import az.azcup.backend.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Şagirdin test həll etməsi: siyahı, başlamaq, təhvil vermək, nəticəyə baxmaq. İstənilən giriş etmiş istifadəçiyə açıqdır
// (SecurityConfig-də "/api/**" authenticated). Düzgün cavablar yalnız təhvildən sonra qaytarılır (bax: QuizService).
@RestController
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // Dərc olunmuş testlər və sorğu edənin öz nəticələri.
    @GetMapping("/api/quizzes")
    public List<QuizSummaryDto> list(@AuthenticationPrincipal UserPrincipal principal) {
        return quizService.listPublished(principal.getUser());
    }

    // Testi başlayır (və ya davam edən cəhdi qaytarır); suallar cavabsız gəlir.
    @PostMapping("/api/quizzes/{id}/attempts")
    public QuizAttemptStartDto start(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return quizService.start(id, principal.getUser());
    }

    // Cavabları təhvil verir; qiymətləndirilmiş nəticə (düzgün cavab və izahlarla) qaytarılır.
    @PostMapping("/api/quiz-attempts/{id}/submit")
    public QuizResultDto submit(
        @PathVariable Long id,
        @Valid @RequestBody QuizSubmitRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return quizService.submit(id, request, principal.getUser());
    }

    // Öz təhvil verilmiş cəhdinin nəticəsi.
    @GetMapping("/api/quiz-attempts/{id}")
    public QuizResultDto result(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        return quizService.getResult(id, principal.getUser());
    }
}
