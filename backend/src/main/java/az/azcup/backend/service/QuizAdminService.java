package az.azcup.backend.service;

import az.azcup.backend.dto.quiz.QuizManageDetailDto;
import az.azcup.backend.dto.quiz.QuizManageSummaryDto;
import az.azcup.backend.dto.quiz.QuizResultsDto;
import az.azcup.backend.dto.quiz.QuizUpsertRequest;
import az.azcup.backend.entity.Quiz;
import az.azcup.backend.entity.QuizAnswer;
import az.azcup.backend.entity.QuizAttempt;
import az.azcup.backend.entity.QuizQuestion;
import az.azcup.backend.entity.QuizQuestionType;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import az.azcup.backend.exception.ApiException;
import az.azcup.backend.exception.ConflictException;
import az.azcup.backend.exception.ForbiddenException;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.repository.GroupMemberRepository;
import az.azcup.backend.repository.QuizAnswerRepository;
import az.azcup.backend.repository.QuizAttemptRepository;
import az.azcup.backend.repository.QuizRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// Müəllim/admin tərəfi: test yaratmaq, redaktə etmək, silmək və nəticələrə baxmaq.
// Sahiblik: müəllim yalnız öz testini idarə edir, admin hamısını. Nəticələr müəllim üçün YALNIZ öz qruplarındakı şagirdlərlə məhdudlaşır.
//
// Test artıq həll olunubsa (təhvil verilmiş cəhd varsa) "kilidlənir": sual silmək, sualın növünü, variant sayını və düzgün cavabı
// dəyişmək qadağandır — əks halda köhnə nəticələr yeni cavab açarı ilə uyğunsuz qalardı. Mətn/izah düzəlişləri və yeni sual əlavəsi icazəlidir.
@Service
public class QuizAdminService {

    private static final int MIN_OPTIONS = 2;
    private static final int MAX_OPTIONS = 6;

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuizAnswerRepository answerRepository;
    private final GroupMemberRepository groupMemberRepository;

    public QuizAdminService(
        QuizRepository quizRepository,
        QuizAttemptRepository attemptRepository,
        QuizAnswerRepository answerRepository,
        GroupMemberRepository groupMemberRepository
    ) {
        this.quizRepository = quizRepository;
        this.attemptRepository = attemptRepository;
        this.answerRepository = answerRepository;
        this.groupMemberRepository = groupMemberRepository;
    }

    // Admin bütün testləri görür. Müəllim öz testlərini və dərc olunmuş hazır testləri görür (hazırlarda yalnız nəticələrə baxır).
    @Transactional(readOnly = true)
    public List<QuizManageSummaryDto> list(User user) {
        List<Quiz> quizzes;
        if (user.getRole() == Role.ADMIN) {
            quizzes = quizRepository.findAllByOrderByUpdatedAtDesc();
        } else {
            quizzes = new ArrayList<>(quizRepository.findByCreatedByOrderByUpdatedAtDesc(user));
            Set<Long> own = quizzes.stream().map(Quiz::getId).collect(Collectors.toSet());
            for (Quiz seeded : quizRepository.findBySeedKeyIsNotNullAndPublishedTrueOrderByTitleAsc()) {
                if (!own.contains(seeded.getId())) {
                    quizzes.add(seeded);
                }
            }
        }
        List<QuizManageSummaryDto> result = new ArrayList<>();
        for (Quiz q : quizzes) {
            result.add(new QuizManageSummaryDto(
                q.getId(), q.getTitle(), q.getCategory(), q.isPublished(), q.getTimeLimitMinutes(), q.getQuestions().size(),
                attemptRepository.countByQuizAndSubmittedAtIsNotNull(q), q.getCreatedBy().getFullName(), q.getUpdatedAt(),
                q.getSeedKey() != null, canEdit(q, user)
            ));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public QuizManageDetailDto get(Long id, User user) {
        return toDetail(getOwned(id, user));
    }

    @Transactional
    public QuizManageDetailDto create(User user, QuizUpsertRequest request) {
        return createInternal(user, request, null);
    }

    // Layihə ilə gələn hazır testi yaradır (bax: QuizSeedLoader): açar eyni tranzaksiyada yazılır ki, yarımçıq nəticə qalmasın.
    @Transactional
    public QuizManageDetailDto createSeeded(User owner, String seedKey, QuizUpsertRequest request) {
        return createInternal(owner, request, seedKey);
    }

    private QuizManageDetailDto createInternal(User user, QuizUpsertRequest request, String seedKey) {
        List<Normalized> items = normalizeAll(request);
        Quiz quiz = new Quiz();
        quiz.setCreatedBy(user);
        quiz.setSeedKey(seedKey);
        applyMeta(quiz, request, items.size());
        for (int i = 0; i < items.size(); i++) {
            QuizQuestion q = new QuizQuestion();
            q.setQuiz(quiz);
            applyQuestion(q, items.get(i), i);
            quiz.getQuestions().add(q);
        }
        return toDetail(quizRepository.save(quiz));
    }

    // Testi bütöv şəkildə yeniləyir (bax: QuizUpsertRequest). Kilidlənmiş testdə qadağan olunan dəyişikliklər 409 ilə rədd edilir.
    @Transactional
    public QuizManageDetailDto update(Long id, User user, QuizUpsertRequest request) {
        Quiz quiz = getOwned(id, user);
        List<Normalized> items = normalizeAll(request);
        boolean locked = attemptRepository.countByQuizAndSubmittedAtIsNotNull(quiz) > 0;

        Map<Long, QuizQuestion> existing = quiz.getQuestions().stream()
            .collect(Collectors.toMap(QuizQuestion::getId, Function.identity()));
        Set<Long> kept = new HashSet<>();
        List<QuizQuestion> next = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Normalized n = items.get(i);
            QuizQuestion q;
            if (n.id() != null) {
                q = existing.get(n.id());
                if (q == null) {
                    throw badRequest("Sual " + (i + 1) + ": bu testə aid deyil");
                }
                if (!kept.add(n.id())) {
                    throw badRequest("Sual " + (i + 1) + ": eyni sual iki dəfə göndərilib");
                }
                if (locked) {
                    requireSameAnswerKey(q, n, i + 1);
                }
            } else {
                q = new QuizQuestion();
                q.setQuiz(quiz);
            }
            applyQuestion(q, n, i);
            next.add(q);
        }
        if (locked && kept.size() < existing.size()) {
            throw new ConflictException(LOCKED_MESSAGE + " Sual silmək olmaz.");
        }
        applyMeta(quiz, request, items.size());
        quiz.getQuestions().clear();
        quiz.getQuestions().addAll(next);
        quiz.setUpdatedAt(Instant.now());
        // Yeni suallara ID-lər (IDENTITY) yalnız flush zamanı verilir; cavabda null qalmasın deyə burada yazdırırıq.
        quizRepository.flush();
        return toDetail(quiz);
    }

    @Transactional
    public void delete(Long id, User user) {
        Quiz quiz = getOwned(id, user);
        if (quiz.getSeedKey() != null) {
            // Silinsə, yükləyici növbəti başlanğıcda onu yenidən yaradardı; ona görə hazır testlər yalnız gizlədilir.
            throw new ConflictException("Bu hazır testdir, silmək olmaz. Şagirdlərdən gizlətmək üçün dərcdən çıxarın.");
        }
        if (attemptRepository.countByQuizAndSubmittedAtIsNotNull(quiz) > 0) {
            throw new ConflictException("Bu testi artıq həll ediblər, ona görə silmək olmaz. Şagirdlərdən gizlətmək üçün dərcdən çıxarın.");
        }
        attemptRepository.deleteByQuiz(quiz);
        quizRepository.delete(quiz);
    }

    // Şagirdlər üzrə ən yaxşı nəticə və sual üzrə doğru cavab faizi. Müəllim yalnız öz qruplarındakı şagirdləri görür; buna görə
    // nəticələrə sahibi olmadığı DƏRC OLUNMUŞ testdə də baxa bilər (şagirdləri istənilən dərc olunmuş testi həll edir), dərc olunmamışda yox.
    @Transactional(readOnly = true)
    public QuizResultsDto results(Long id, User user) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new NotFoundException("Test tapılmadı: " + id));
        if (!quiz.isPublished() && !canEdit(quiz, user)) {
            throw new ForbiddenException("Bu test sizə aid deyil");
        }
        boolean all = user.getRole() == Role.ADMIN;
        Set<Long> scope = all ? null : new HashSet<>(groupMemberRepository.findStudentIdsByTeacherId(user.getId()));
        List<QuizAttempt> attempts = attemptRepository.findByQuizAndSubmittedAtIsNotNullOrderBySubmittedAtDesc(quiz).stream()
            .filter(a -> a.getStudent().getRole() == Role.STUDENT)
            .filter(a -> all || scope.contains(a.getStudent().getId()))
            .toList();

        // Cəhdlər ən yenidən köhnəyə doğrudur, ona görə hər şagirdin ilk gördüyümüz cəhdi onun sonuncusudur.
        Map<Long, List<QuizAttempt>> byStudent = new LinkedHashMap<>();
        for (QuizAttempt a : attempts) {
            byStudent.computeIfAbsent(a.getStudent().getId(), k -> new ArrayList<>()).add(a);
        }
        List<QuizResultsDto.StudentRow> students = new ArrayList<>();
        for (List<QuizAttempt> mine : byStudent.values()) {
            QuizAttempt best = mine.stream()
                .max(Comparator.<QuizAttempt>comparingInt(a -> QuizService.percent(a.getCorrectCount(), a.getTotalCount()))
                    .thenComparingInt(QuizAttempt::getCorrectCount))
                .orElseThrow();
            User student = mine.get(0).getStudent();
            students.add(new QuizResultsDto.StudentRow(
                student.getId(), student.getFullName(), student.getEmail(), mine.size(),
                best.getCorrectCount(), best.getTotalCount(), QuizService.percent(best.getCorrectCount(), best.getTotalCount()),
                mine.get(0).getSubmittedAt()
            ));
        }
        students.sort(Comparator.comparing(r -> r.name() == null ? "" : r.name().toLowerCase(Locale.ROOT)));

        List<QuizAnswer> answers = attempts.isEmpty() ? List.of() : answerRepository.findByAttemptIn(attempts);
        List<QuizResultsDto.QuestionStat> stats = new ArrayList<>();
        for (QuizQuestion q : quiz.getQuestions()) {
            int rows = 0;
            int answered = 0;
            int correct = 0;
            for (QuizAnswer a : answers) {
                if (!a.getQuestion().getId().equals(q.getId())) {
                    continue;
                }
                rows++;
                if (a.getAnswer() != null) {
                    answered++;
                }
                if (a.isCorrect()) {
                    correct++;
                }
            }
            stats.add(new QuizResultsDto.QuestionStat(
                q.getId(), q.getPosition(), shorten(q.getPrompt(), 120), answered, correct, QuizService.percent(correct, rows)
            ));
        }
        return new QuizResultsDto(quiz.getTitle(), quiz.getQuestions().size(), all ? "ALL" : "MY_GROUPS", students, stats);
    }

    // ---- yardımçılar ----

    private static final String LOCKED_MESSAGE =
        "Bu testi artıq həll ediblər: düzgün cavabı, sualın növünü və variant sayını dəyişmək, sual silmək olmaz. Fərqli test üçün yenisini yaradın.";

    private Quiz getOwned(Long id, User user) {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new NotFoundException("Test tapılmadı: " + id));
        if (!canEdit(quiz, user)) {
            throw new ForbiddenException("Bu test sizə aid deyil");
        }
        return quiz;
    }

    // Sahibi və admin testi redaktə edə bilər.
    private static boolean canEdit(Quiz quiz, User user) {
        return user.getRole() == Role.ADMIN || quiz.getCreatedBy().getId().equals(user.getId());
    }

    private void applyMeta(Quiz quiz, QuizUpsertRequest request, int questionCount) {
        String title = request.title() == null ? "" : request.title().trim();
        if (title.isEmpty()) {
            throw badRequest("Testin adı boş ola bilməz");
        }
        if (request.published() && questionCount == 0) {
            throw badRequest("Dərc etmək üçün ən azı bir sual olmalıdır");
        }
        quiz.setTitle(title);
        quiz.setDescription(blankToNull(request.description()));
        quiz.setCategory(blankToNull(request.category()));
        quiz.setTimeLimitMinutes(request.timeLimitMinutes());
        quiz.setPublished(request.published());
    }

    // Sualın yoxlanılmış və təmizlənmiş halı.
    private record Normalized(
        Long id, QuizQuestionType type, String prompt, String code, List<String> options,
        Integer correctIndex, String correctText, String explanation
    ) {
    }

    private List<Normalized> normalizeAll(QuizUpsertRequest request) {
        List<Normalized> result = new ArrayList<>();
        List<QuizUpsertRequest.QuestionUpsert> list = request.questions() == null ? List.of() : request.questions();
        for (int i = 0; i < list.size(); i++) {
            result.add(normalize(list.get(i), i + 1));
        }
        return result;
    }

    private Normalized normalize(QuizUpsertRequest.QuestionUpsert u, int number) {
        String where = "Sual " + number + ": ";
        QuizQuestionType type;
        try {
            type = QuizQuestionType.valueOf(u.type().trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw badRequest(where + "naməlum növ (CHOICE və ya NUMERIC olmalıdır)");
        }
        String prompt = u.prompt() == null ? "" : u.prompt().trim();
        if (prompt.isEmpty()) {
            throw badRequest(where + "sualın mətni boşdur");
        }
        String code = u.code() == null || u.code().isBlank() ? null : u.code().stripTrailing();
        String explanation = blankToNull(u.explanation());

        if (type == QuizQuestionType.CHOICE) {
            List<String> options = new ArrayList<>();
            for (String o : u.options() == null ? List.<String>of() : u.options()) {
                String t = o == null ? "" : o.trim();
                if (t.isEmpty()) {
                    throw badRequest(where + "variant boş ola bilməz");
                }
                options.add(t);
            }
            if (options.size() < MIN_OPTIONS || options.size() > MAX_OPTIONS) {
                throw badRequest(where + "variantların sayı " + MIN_OPTIONS + "–" + MAX_OPTIONS + " arasında olmalıdır");
            }
            Integer idx = u.correctIndex();
            if (idx == null || idx < 0 || idx >= options.size()) {
                throw badRequest(where + "düzgün variant seçilməyib");
            }
            return new Normalized(u.id(), type, prompt, code, options, idx, null, explanation);
        }

        String text = u.correctText() == null ? "" : u.correctText().trim();
        if (text.isEmpty()) {
            throw badRequest(where + "düzgün cavab yazılmayıb");
        }
        for (String part : text.split("\\|", -1)) {
            if (part.isBlank()) {
                throw badRequest(where + "düzgün cavabda boş seçim var (\"|\" ilə ayrılan hissələr boş ola bilməz)");
            }
        }
        return new Normalized(u.id(), type, prompt, code, List.of(), null, text, explanation);
    }

    // Kilidlənmiş testdə cavab açarı dəyişə bilməz: növ, variant sayı, düzgün variant, düzgün mətn eyni qalmalıdır.
    private void requireSameAnswerKey(QuizQuestion q, Normalized n, int number) {
        boolean same = q.getType() == n.type()
            && q.getOptions().size() == n.options().size()
            && Objects.equals(q.getCorrectIndex(), n.correctIndex())
            && Objects.equals(q.getCorrectText(), n.correctText());
        if (!same) {
            throw new ConflictException("Sual " + number + ": " + LOCKED_MESSAGE);
        }
    }

    private void applyQuestion(QuizQuestion q, Normalized n, int position) {
        q.setPosition(position);
        q.setType(n.type());
        q.setPrompt(n.prompt());
        q.setCode(n.code());
        q.getOptions().clear();
        q.getOptions().addAll(n.options());
        q.setCorrectIndex(n.correctIndex());
        q.setCorrectText(n.correctText());
        q.setExplanation(n.explanation());
    }

    private QuizManageDetailDto toDetail(Quiz quiz) {
        long attempts = quiz.getId() == null ? 0 : attemptRepository.countByQuizAndSubmittedAtIsNotNull(quiz);
        List<QuizManageDetailDto.Question> questions = new ArrayList<>();
        for (QuizQuestion q : quiz.getQuestions()) {
            questions.add(new QuizManageDetailDto.Question(
                q.getId(), q.getPosition(), q.getType().name(), q.getPrompt(), q.getCode(), List.copyOf(q.getOptions()),
                q.getCorrectIndex(), q.getCorrectText(), q.getExplanation()
            ));
        }
        return new QuizManageDetailDto(
            quiz.getId(), quiz.getTitle(), quiz.getDescription(), quiz.getCategory(), quiz.getTimeLimitMinutes(),
            quiz.isPublished(), quiz.getCreatedBy().getFullName(), attempts, attempts > 0, questions
        );
    }

    private static ApiException badRequest(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, message);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static String shorten(String s, int max) {
        String oneLine = s.replaceAll("\\s+", " ").trim();
        return oneLine.length() <= max ? oneLine : oneLine.substring(0, max - 1) + "…";
    }
}
