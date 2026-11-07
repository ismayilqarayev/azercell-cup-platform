package az.azcup.backend.service;

import az.azcup.backend.dto.quiz.QuizAttemptStartDto;
import az.azcup.backend.dto.quiz.QuizQuestionDto;
import az.azcup.backend.dto.quiz.QuizResultDto;
import az.azcup.backend.dto.quiz.QuizReviewItemDto;
import az.azcup.backend.dto.quiz.QuizSubmitRequest;
import az.azcup.backend.dto.quiz.QuizSummaryDto;
import az.azcup.backend.entity.Quiz;
import az.azcup.backend.entity.QuizAnswer;
import az.azcup.backend.entity.QuizAttempt;
import az.azcup.backend.entity.QuizQuestion;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import az.azcup.backend.exception.ConflictException;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.repository.QuizAnswerRepository;
import az.azcup.backend.repository.QuizAttemptRepository;
import az.azcup.backend.repository.QuizRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// Şagird tərəfi: dərc olunmuş testlərin siyahısı, testi başlamaq, cavabları təhvil vermək və nəticəyə baxmaq.
// Düzgün cavablar və izahlar YALNIZ təhvildən sonra (nəticə cavabında) çıxır; həll zamanı verilən suallarda onlar yoxdur.
@Service
public class QuizService {

    // Vaxt bitəndən sonra bu qədər saniyə gecikmə "gec" sayılmır (şəbəkə gecikməsi üçün).
    static final long LATE_GRACE_SECONDS = 60;

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuizAnswerRepository answerRepository;

    public QuizService(
        QuizRepository quizRepository,
        QuizAttemptRepository attemptRepository,
        QuizAnswerRepository answerRepository
    ) {
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
    }

    // Dərc olunmuş testlər, hər biri üçün sorğu edən şagirdin öz nəticə məlumatı ilə.
    @Transactional(readOnly = true)
    public List<QuizSummaryDto> listPublished(User user) {
        Map<Long, List<QuizAttempt>> submitted = attemptRepository.findByStudentAndSubmittedAtIsNotNull(user).stream()
            .collect(Collectors.groupingBy(a -> a.getQuiz().getId()));
        Set<Long> open = attemptRepository.findByStudentAndSubmittedAtIsNull(user).stream()
            .map(a -> a.getQuiz().getId())
            .collect(Collectors.toSet());
        List<QuizSummaryDto> result = new ArrayList<>();
        for (Quiz quiz : quizRepository.findByPublishedTrueOrderByCreatedAtDesc()) {
            List<QuizAttempt> mine = submitted.getOrDefault(quiz.getId(), List.of());
            Integer best = mine.stream()
                .filter(a -> a.getTotalCount() > 0)
                .map(a -> percent(a.getCorrectCount(), a.getTotalCount()))
                .max(Comparator.naturalOrder())
                .orElse(null);
            result.add(new QuizSummaryDto(
                quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getCategory(), quiz.getTimeLimitMinutes(),
                quiz.getQuestions().size(), mine.size(), best, open.contains(quiz.getId())
            ));
        }
        return result;
    }

    // Testi başlayır (və ya davam edən cəhdi qaytarır). Vaxtı keçmiş davam edən cəhd yenidən başladılır.
    @Transactional
    public QuizAttemptStartDto start(Long quizId, User user) {
        Quiz quiz = quizRepository.findById(quizId)
            .orElseThrow(() -> new NotFoundException("Test tapılmadı: " + quizId));
        // Dərc olunmamış testi yalnız sahibi/admin yoxlaya bilər; başqaları üçün o, mövcud deyil.
        if (!quiz.isPublished() && !canManage(quiz, user)) {
            throw new NotFoundException("Test tapılmadı: " + quizId);
        }
        if (quiz.getQuestions().isEmpty()) {
            throw new ConflictException("Bu testdə hələ sual yoxdur");
        }
        Instant now = Instant.now();
        QuizAttempt attempt = attemptRepository
            .findFirstByQuizAndStudentAndSubmittedAtIsNullOrderByStartedAtDesc(quiz, user)
            .orElse(null);
        if (attempt == null) {
            attempt = new QuizAttempt();
            attempt.setQuiz(quiz);
            attempt.setStudent(user);
            attempt.setStartedAt(now);
            attempt = attemptRepository.save(attempt);
        } else if (isExpired(attempt, now)) {
            attempt.setStartedAt(now);
        }
        List<QuizQuestionDto> questions = new ArrayList<>();
        for (QuizQuestion q : quiz.getQuestions()) {
            questions.add(new QuizQuestionDto(
                q.getId(), q.getPosition(), q.getType().name(), q.getPrompt(), q.getCode(), List.copyOf(q.getOptions())
            ));
        }
        return new QuizAttemptStartDto(
            attempt.getId(), quiz.getId(), quiz.getTitle(), quiz.getTimeLimitMinutes(),
            attempt.getStartedAt(), deadlineOf(attempt), now, questions
        );
    }

    // Cavabları qəbul edib qiymətləndirir. Eyni cəhd yalnız bir dəfə təhvil verilə bilər.
    @Transactional
    public QuizResultDto submit(Long attemptId, QuizSubmitRequest request, User user) {
        QuizAttempt attempt = attemptRepository.findByIdForUpdate(attemptId)
            .orElseThrow(() -> new NotFoundException("Cəhd tapılmadı: " + attemptId));
        requireOwner(attempt, user);
        if (attempt.getSubmittedAt() != null) {
            throw new ConflictException("Bu cəhd artıq təhvil verilib");
        }
        Quiz quiz = attempt.getQuiz();
        Instant now = Instant.now();
        Instant deadline = deadlineOf(attempt);
        boolean late = deadline != null && now.isAfter(deadline.plusSeconds(LATE_GRACE_SECONDS));

        Map<Long, String> given = new HashMap<>();
        for (QuizSubmitRequest.AnswerItem item : request.answers()) {
            if (item != null && item.questionId() != null) {
                given.put(item.questionId(), blankToNull(item.answer()));
            }
        }
        List<QuizAnswer> rows = new ArrayList<>();
        int correct = 0;
        for (QuizQuestion question : quiz.getQuestions()) {
            String answer = given.get(question.getId());
            boolean ok = QuizGrader.isCorrect(question, answer);
            if (ok) {
                correct++;
            }
            QuizAnswer row = new QuizAnswer();
            row.setAttempt(attempt);
            row.setQuestion(question);
            row.setAnswer(answer);
            row.setCorrect(ok);
            rows.add(row);
        }
        answerRepository.saveAll(rows);
        attempt.setCorrectCount(correct);
        attempt.setTotalCount(rows.size());
        attempt.setLate(late);
        attempt.setSubmittedAt(now);
        return toResult(attempt, rows);
    }

    // Öz təhvil verilmiş cəhdinin nəticəsi (izahlarla). Başqasının cəhdinə baxmaq olmaz.
    @Transactional(readOnly = true)
    public QuizResultDto getResult(Long attemptId, User user) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
            .orElseThrow(() -> new NotFoundException("Cəhd tapılmadı: " + attemptId));
        requireOwner(attempt, user);
        if (attempt.getSubmittedAt() == null) {
            throw new ConflictException("Cəhd hələ təhvil verilməyib");
        }
        return toResult(attempt, answerRepository.findByAttempt(attempt));
    }

    private QuizResultDto toResult(QuizAttempt attempt, List<QuizAnswer> rows) {
        List<QuizReviewItemDto> items = rows.stream()
            .sorted(Comparator.comparingInt(r -> r.getQuestion().getPosition()))
            .map(r -> {
                QuizQuestion q = r.getQuestion();
                return new QuizReviewItemDto(
                    q.getId(), q.getPosition(), q.getType().name(), q.getPrompt(), q.getCode(), List.copyOf(q.getOptions()),
                    r.getAnswer(), r.isCorrect(), q.getCorrectIndex(), q.getCorrectText(), q.getExplanation()
                );
            })
            .toList();
        Quiz quiz = attempt.getQuiz();
        return new QuizResultDto(
            attempt.getId(), quiz.getId(), quiz.getTitle(), attempt.getCorrectCount(), attempt.getTotalCount(),
            percent(attempt.getCorrectCount(), attempt.getTotalCount()), attempt.isLate(),
            attempt.getStartedAt(), attempt.getSubmittedAt(), items
        );
    }

    private void requireOwner(QuizAttempt attempt, User user) {
        if (!attempt.getStudent().getId().equals(user.getId())) {
            // 403 yox, 404: başqasının cəhdinin mövcudluğu da açılmasın.
            throw new NotFoundException("Cəhd tapılmadı: " + attempt.getId());
        }
    }

    private static boolean canManage(Quiz quiz, User user) {
        return user.getRole() == Role.ADMIN
            || (user.getRole() == Role.TEACHER && quiz.getCreatedBy().getId().equals(user.getId()));
    }

    private static Instant deadlineOf(QuizAttempt attempt) {
        Integer minutes = attempt.getQuiz().getTimeLimitMinutes();
        return minutes == null ? null : attempt.getStartedAt().plus(minutes, ChronoUnit.MINUTES);
    }

    private static boolean isExpired(QuizAttempt attempt, Instant now) {
        Instant deadline = deadlineOf(attempt);
        return deadline != null && now.isAfter(deadline.plusSeconds(LATE_GRACE_SECONDS));
    }

    static int percent(int correct, int total) {
        return total <= 0 ? 0 : (correct * 100 + total / 2) / total;
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }
}
