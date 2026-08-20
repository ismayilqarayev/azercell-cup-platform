package az.azcup.backend.service;

import az.azcup.backend.dto.ProgressDto;
import az.azcup.backend.dto.SubmissionResponse;
import az.azcup.backend.entity.Problem;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.Submission;
import az.azcup.backend.entity.Topic;
import az.azcup.backend.entity.User;
import az.azcup.backend.judge.JudgeResult;
import az.azcup.backend.judge.JudgeService;
import az.azcup.backend.repository.ProblemRepository;
import az.azcup.backend.repository.SubmissionRepository;
import az.azcup.backend.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    // Kodu compile edib icra etmək üçün mərkəzi yoxlayıcı servis.
    private final JudgeService judgeService;

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public SubmissionService(
        SubmissionRepository submissionRepository,
        ProblemService problemService,
        TopicRepository topicRepository,
        ProblemRepository problemRepository,
        JudgeService judgeService
    ) {
        this.submissionRepository = submissionRepository;
        this.problemService = problemService;
        this.topicRepository = topicRepository;
        this.problemRepository = problemRepository;
        this.judgeService = judgeService;
    }

    // Kodu qəbul edir, compile+icra etdirir (JudgeService.judge) və nəticəni
    // (uğurlu da olsa, uğursuz da) DAİMİ olaraq bazaya yazır — beləliklə
    // tarixçə (history) heç vaxt boş qalmır, hətta kompilyasiya xətası olsa belə.
    @Transactional
    public SubmissionResponse submit(Long problemId, User user, String sourceCode) {
        // getEntity() daxildə "bu problem hələ açıq olmayan mövzudadırsa,
        // STUDENT ona kod göndərə bilməz" qaydasını da tətbiq edir.
        Problem problem = problemService.getEntity(problemId, user);
        JudgeResult result = judgeService.judge(sourceCode, problem);

        Submission submission = new Submission();
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setSourceCode(sourceCode);
        submission.setStatus(result.getStatus());
        submission.setStdout(result.getStdout());
        submission.setStderr(result.getStderr());
        submission.setExecutionTimeMs(result.getExecutionTimeMs());
        submissionRepository.save(submission);
        return toResponse(submission);
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
            s.getStdout(), s.getStderr(), s.getExecutionTimeMs(), s.getSubmittedAt()
        );
    }
}
