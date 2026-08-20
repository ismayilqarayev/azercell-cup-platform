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

    private final SubmissionRepository submissionRepository;
    private final ProblemService problemService;
    private final TopicRepository topicRepository;
    private final ProblemRepository problemRepository;
    private final JudgeService judgeService;

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
        List<Topic> topics = new ArrayList<>();
        for (Topic t : allTopics) {
            if (user.getRole() != Role.STUDENT || t.isPublished()) {
                topics.add(t);
            }
        }
        Map<Long, Long> solvedByTopicId = new HashMap<>();
        for (SubmissionRepository.TopicSolvedCount tsc : submissionRepository.solvedCountsByTopicForUser(user)) {
            solvedByTopicId.put(tsc.getTopicId(), tsc.getSolvedCount());
        }
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

    private SubmissionResponse toResponse(Submission s) {
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
