package az.azcup.backend.service;

import az.azcup.backend.dto.ActivityDayDto;
import az.azcup.backend.dto.ProgressDto;
import az.azcup.backend.dto.SubmissionResponse;
import az.azcup.backend.entity.ExampleCompletion;
import az.azcup.backend.entity.Problem;
import az.azcup.backend.entity.ProblemTestCase;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.Submission;
import az.azcup.backend.entity.Topic;
import az.azcup.backend.entity.User;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.judge.JudgeService;
import az.azcup.backend.judge.MultiJudgeResult;
import az.azcup.backend.judge.TestCaseInput;
import az.azcup.backend.repository.ExampleCompletionRepository;
import az.azcup.backend.repository.ProblemRepository;
import az.azcup.backend.repository.ProblemTestCaseRepository;
import az.azcup.backend.repository.SubmissionRepository;
import az.azcup.backend.repository.TopicRepository;
import az.azcup.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// Şagirdin kod göndərməsini idarə edir: yoxlatdırır (JudgeService), nəticəni
// bazaya yazır və irəliləyiş statistikasını hesablayır.
@Service
public class SubmissionService {

    // Təqdimatları oxumaq/yazmaq üçün.
    private final SubmissionRepository submissionRepository;
    // Problem entity-sini tapmaq və giriş icazəsini yoxlamaq üçün.
    private final ProblemService problemService;
    // Mövzuları oxumaq üçün (progress() metodunda istifadə olunur).
    private final TopicRepository topicRepository;
    // Hər mövzudakı ümumi problem sayını hesablamaq üçün.
    private final ProblemRepository problemRepository;
    // Problemin nümunə cütündən əlavə, admin tərəfindən əlavə edilmiş
    // gizli test hallarını oxumaq üçün (bax: submit metodundakı istifadə).
    private final ProblemTestCaseRepository problemTestCaseRepository;
    // Kodu compile edib icra etmək üçün mərkəzi yoxlayıcı servis.
    private final JudgeService judgeService;
    // Müəllimin fəaliyyət xəritəsinə baxdığı şagirdi ID-sinə görə tapmaq üçün.
    private final UserRepository userRepository;
    // Tapşırıqlardakı kod nümunəsi tamamlanmaları da fəaliyyət xəritəsinə (heatmap) sayılır.
    private final ExampleCompletionRepository exampleCompletionRepository;

    // Fəaliyyət xəritəsi neçə günü (GitHub-dakı kimi ~ son 53 həftə) əhatə edir.
    private static final int ACTIVITY_DAYS = 371;
    // Günlər BU saat qurşağına görə qruplaşdırılır ki, gecə saatlarındakı
    // cəhdlər səhv olaraq "növbəti günə" düşməsin (istifadəçilərin əksəriyyəti Azərbaycandadır).
    private static final ZoneId ACTIVITY_ZONE = ZoneId.of("Asia/Baku");
    private static final DateTimeFormatter ACTIVITY_DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public SubmissionService(
        SubmissionRepository submissionRepository,
        ProblemService problemService,
        TopicRepository topicRepository,
        ProblemRepository problemRepository,
        ProblemTestCaseRepository problemTestCaseRepository,
        JudgeService judgeService,
        UserRepository userRepository,
        ExampleCompletionRepository exampleCompletionRepository
    ) {
        this.exampleCompletionRepository = exampleCompletionRepository;
        this.submissionRepository = submissionRepository;
        this.problemService = problemService;
        this.topicRepository = topicRepository;
        this.problemRepository = problemRepository;
        this.problemTestCaseRepository = problemTestCaseRepository;
        this.judgeService = judgeService;
        this.userRepository = userRepository;
    }

    // Kodu qəbul edir, compile+icra etdirir və nəticəni (uğurlu da olsa,
    // uğursuz da) DAİMİ olaraq bazaya yazır — beləliklə tarixçə (history)
    // heç vaxt boş qalmır, hətta kompilyasiya xətası olsa belə.
    //
    // Yoxlama HƏMİŞƏ JudgeService.judgeMultiple ilə aparılır (yarışlarda
    // istifadə olunan eyni mexanizm) — nümunə cütü 0-cı test halı, admin
    // tərəfindən əlavə edilmiş ProblemTestCase sətirləri isə sonrakı gizli
    // test halları kimi bir siyahıya toplanır. Əksər problemlərdə əlavə test
    // halı YOXDUR — bu halda siyahıda cəmi bir element olur və davranış
    // əvvəlki (tək test halı) davranışı ilə eynidir.
    @Transactional
    public SubmissionResponse submit(Long problemId, User user, String sourceCode) {
        // getEntity() daxildə "bu problem hələ açıq olmayan mövzudadırsa,
        // STUDENT ona kod göndərə bilməz" qaydasını da tətbiq edir.
        Problem problem = problemService.getEntity(problemId, user);
        List<TestCaseInput> testCases = buildTestCases(problem);
        MultiJudgeResult result = judgeService.judgeMultiple(sourceCode, testCases);

        Submission submission = new Submission();
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setSourceCode(sourceCode);
        submission.setStatus(result.getStatus());
        submission.setPassedTestCases(result.getPassedTestCases());
        submission.setTotalTestCases(result.getTotalTestCases());
        submission.setFirstFailedTestCaseOrder(result.getFirstFailedTestCaseOrder());
        submission.setStdout(result.getStdout());
        submission.setStderr(result.getStderr());
        submission.setExecutionTimeMs(result.getExecutionTimeMs());
        submissionRepository.save(submission);
        return toResponse(submission);
    }

    // Problemin nümunə cütünü 0-cı test halı kimi, admin tərəfindən əlavə
    // edilmiş gizli ProblemTestCase sətirlərini isə ardıcıl sıra ilə
    // sonrakı test halları kimi bir siyahıya toplayır.
    private List<TestCaseInput> buildTestCases(Problem problem) {
        List<TestCaseInput> testCases = new ArrayList<>();
        testCases.add(new TestCaseInput(0, problem.getExampleInput(), problem.getExampleOutput()));
        int order = 1;
        for (ProblemTestCase tc : problemTestCaseRepository.findByProblemOrderByOrderIndexAsc(problem)) {
            testCases.add(new TestCaseInput(order, tc.getInput(), tc.getExpectedOutput()));
            order++;
        }
        return testCases;
    }

    // Bu istifadəçinin bu problemə etdiyi bütün cəhdlərin tarixçəsi.
    @Transactional(readOnly = true)
    public List<SubmissionResponse> history(Long problemId, User user) {
        Problem problem = problemService.getEntity(problemId, user);
        List<Submission> submissions = submissionRepository.findByUserAndProblemOrderBySubmittedAtDesc(user, problem);
        // Hər Submission entity-sini API-ya göstərilən SubmissionResponse formasına çevirir.
        List<SubmissionResponse> result = new ArrayList<>();
        for (Submission s : submissions) {
            result.add(toResponse(s));
        }
        return result;
    }

    // Hər mövzu üzrə "cəmi neçə problem var, onlardan neçəsini bu istifadəçi
    // həll edib" statistikası — TopicService.listTopics-dəki eyni məntiqin
    // (STUDENT üçün yalnız dərc olunmuş mövzuları filtrləmə) bir daha
    // istifadəsi, sadəcə fərqli DTO (ProgressDto) formasında.
    @Transactional(readOnly = true)
    public List<ProgressDto> progress(User user) {
        List<Topic> allTopics = topicRepository.findAllByOrderByOrderIndexAsc();
        // STUDENT üçün yalnız dərc olunmuş (published) mövzular daxil edilir —
        // TEACHER/ADMIN isə hamısını görür (bax: TopicService.listTopics-dəki eyni qayda).
        List<Topic> topics = new ArrayList<>();
        for (Topic t : allTopics) {
            if (user.getRole() != Role.STUDENT || t.isPublished()) {
                topics.add(t);
            }
        }
        // Sorğunun (topicId, solvedCount) nəticələrini sürətli axtarış üçün map-ə çevirir.
        Map<Long, Long> solvedByTopicId = new HashMap<>();
        for (SubmissionRepository.TopicSolvedCount tsc : submissionRepository.solvedCountsByTopicForUser(user)) {
            solvedByTopicId.put(tsc.getTopicId(), tsc.getSolvedCount());
        }
        // Hər mövzunu, ümumi/həll edilmiş problem sayları ilə birlikdə ProgressDto-ya çevirir.
        List<ProgressDto> result = new ArrayList<>();
        for (Topic t : topics) {
            result.add(new ProgressDto(
                t.getSlug(),
                t.getTitle(),
                problemRepository.countByTopic(t),
                solvedByTopicId.getOrDefault(t.getId(), 0L)
            ));
        }
        return result;
    }

    // Şagirdin ÖZ fəaliyyət xəritəsi ("Hazırlıq Planı" landing-ində GitHub-un
    // "contribution graph"ına bənzər gündəlik cəhd sayğacı).
    @Transactional(readOnly = true)
    public List<ActivityDayDto> activity(User user) {
        return buildActivity(user);
    }

    // Müəllim/admin panelindən bir şagirdin fəaliyyət xəritəsi — istifadəçini
    // ID-sinə görə tapır (sahiblik yoxlaması yoxdur, çünki eyni "Şagirdlər"
    // siyahısı artıq istənilən TEACHER/ADMIN-ə açıqdır, bax: SecurityConfig).
    @Transactional(readOnly = true)
    public List<ActivityDayDto> activityForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("İstifadəçi tapılmadı: " + userId));
        return buildActivity(user);
    }

    // Son ACTIVITY_DAYS gündəki bütün cəhdləri yerli (Asia/Baku) təqvim
    // gününə görə qruplaşdırıb sayır. Yalnız cəhd OLAN günlər qaytarılır —
    // boş günləri 0 kimi doldurmaq frontend-in işidir (bax: ActivityDayDto).
    // TreeMap istifadə olunur ki, nəticə tarixə görə sıralı gəlsin.
    private List<ActivityDayDto> buildActivity(User user) {
        Instant since = LocalDate.now(ACTIVITY_ZONE).minusDays(ACTIVITY_DAYS - 1L)
            .atStartOfDay(ACTIVITY_ZONE).toInstant();
        Map<String, Integer> countsByDate = new TreeMap<>();
        for (Instant submittedAt : submissionRepository.submittedAtsForUserSince(user, since)) {
            String day = ACTIVITY_DATE_FORMAT.format(submittedAt.atZone(ACTIVITY_ZONE).toLocalDate());
            countsByDate.merge(day, 1, Integer::sum);
        }
        for (ExampleCompletion completion : exampleCompletionRepository.findByStudentAndCompletedAtAfter(user, since)) {
            String day = ACTIVITY_DATE_FORMAT.format(completion.getCompletedAt().atZone(ACTIVITY_ZONE).toLocalDate());
            countsByDate.merge(day, 1, Integer::sum);
        }
        List<ActivityDayDto> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : countsByDate.entrySet()) {
            result.add(new ActivityDayDto(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    // Submission entity-sini API-ya göstərilən SubmissionResponse DTO-suna çevirən köməkçi metod.
    private SubmissionResponse toResponse(Submission s) {
        // problem əlaqəsi nəzəri olaraq null ola bilər (məs. problem silinibsə) —
        // bu halda problemId də null qalır.
        Long problemId;
        if (s.getProblem() != null) {
            problemId = s.getProblem().getId();
        } else {
            problemId = null;
        }
        return new SubmissionResponse(
            s.getId(), problemId, s.getSourceCode(), s.getStatus(),
            s.getPassedTestCases(), s.getTotalTestCases(), s.getFirstFailedTestCaseOrder(),
            s.getStdout(), s.getStderr(), s.getExecutionTimeMs(), s.getSubmittedAt()
        );
    }
}
