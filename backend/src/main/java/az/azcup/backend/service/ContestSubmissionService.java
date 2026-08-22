package az.azcup.backend.service;

import az.azcup.backend.dto.contest.ContestStatusEnum;
import az.azcup.backend.dto.contest.ContestSubmissionResponse;
import az.azcup.backend.entity.Contest;
import az.azcup.backend.entity.ContestProblem;
import az.azcup.backend.entity.ContestSubmission;
import az.azcup.backend.entity.ContestTestCase;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import az.azcup.backend.exception.ApiException;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.judge.JudgeService;
import az.azcup.backend.judge.MultiJudgeResult;
import az.azcup.backend.judge.TestCaseInput;
import az.azcup.backend.repository.ContestParticipantRepository;
import az.azcup.backend.repository.ContestProblemRepository;
import az.azcup.backend.repository.ContestSubmissionRepository;
import az.azcup.backend.repository.ContestTestCaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

// Şagirdin yarış məsələsinə kod GÖNDƏRMƏSİ ilə bağlı biznes-məntiq —
// SubmissionService-in (practice) yarış qarşılığı. Ən vacib fərq: burada
// "yarış hazırda AKTİVDİRMİ" qadağası SERVER TƏRƏFİNDƏ, hər göndərmədə
// YENİDƏN yoxlanılır (bax: submit metodunun başlanğıcı) — frontend-in
// düyməni gizlətməsinə etibar edilmir, çünki API-yə birbaşa sorğu ilə bu
// yoxlama asanlıqla ötürülə bilərdi.
@Service
public class ContestSubmissionService {

    // Yarış məsələlərini tapmaq üçün.
    private final ContestProblemRepository contestProblemRepository;
    // Bir məsələnin bütün test hallarını (gizli daxil) oxumaq üçün.
    private final ContestTestCaseRepository contestTestCaseRepository;
    // "Şagird bu yarışa qoşulub mu" yoxlaması üçün.
    private final ContestParticipantRepository contestParticipantRepository;
    // Təqdimatları yazmaq/oxumaq üçün.
    private final ContestSubmissionRepository contestSubmissionRepository;
    // Kodu compile edib BÜTÜN test hallarına qarşı icra etmək üçün.
    private final JudgeService judgeService;

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public ContestSubmissionService(
        ContestProblemRepository contestProblemRepository,
        ContestTestCaseRepository contestTestCaseRepository,
        ContestParticipantRepository contestParticipantRepository,
        ContestSubmissionRepository contestSubmissionRepository,
        JudgeService judgeService
    ) {
        this.contestProblemRepository = contestProblemRepository;
        this.contestTestCaseRepository = contestTestCaseRepository;
        this.contestParticipantRepository = contestParticipantRepository;
        this.contestSubmissionRepository = contestSubmissionRepository;
        this.judgeService = judgeService;
    }

    // Kodu qəbul edir, BÜTÜN gizli test hallarına qarşı yoxlatdırır
    // (JudgeService.judgeMultiple) və nəticəni (uğurlu da olsa, uğursuz da)
    // DAİMİ olaraq bazaya yazır — SubmissionService.submit-dəki eyni prinsip.
    @Transactional
    public ContestSubmissionResponse submit(Long contestProblemId, User user, String sourceCode) {
        if (user.getRole() != Role.STUDENT) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Yalnız şagirdlər yarışa kod göndərə bilər");
        }

        ContestProblem contestProblem = contestProblemRepository.findById(contestProblemId)
            .orElseThrow(() -> new NotFoundException("Yarış məsələsi tapılmadı: " + contestProblemId));
        Contest contest = contestProblem.getContest();

        // ƏSAS TƏHLÜKƏSİZLİK YOXLAMASI: yarışın statusu SERVER TƏRƏFİNDƏ,
        // HƏR göndərmədə YENİDƏN hesablanır — bax: ContestService.computeStatus.
        ContestStatusEnum status = ContestService.computeStatus(contest);
        if (status != ContestStatusEnum.ACTIVE) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Bu yarış hazırda aktiv deyil, kod göndərmək mümkün deyil");
        }

        boolean joined = contestParticipantRepository.findByContestAndUser(contest, user).isPresent();
        if (!joined) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Kod göndərmədən əvvəl yarışa qoşulmalısınız");
        }

        List<ContestTestCase> testCases = contestTestCaseRepository.findByContestProblemOrderByOrderIndexAsc(contestProblem);
        List<TestCaseInput> testCaseInputs = new ArrayList<>();
        for (ContestTestCase tc : testCases) {
            testCaseInputs.add(new TestCaseInput(tc.getOrderIndex(), tc.getInput(), tc.getExpectedOutput()));
        }

        MultiJudgeResult result = judgeService.judgeMultiple(sourceCode, testCaseInputs);

        // "Hamısı ya heç nə" bal siyasəti: bu məsələ bu şagird tərəfindən
        // ARTIQ tam həll edilibsə (əvvəlki bir cəhddə bal qazanılıbsa),
        // yenidən düzgün göndərsə belə TƏKRAR bal verilmir — əks halda eyni
        // düzgün kodu dəfələrlə göndərib bal "fermalaşdırmaq" mümkün olardı.
        boolean alreadySolved = contestSubmissionRepository
            .existsByUserAndContestProblemAndPointsAwardedGreaterThan(user, contestProblem, 0);
        int pointsAwarded = 0;
        if (result.getStatus() == az.azcup.backend.entity.SubmissionStatus.ACCEPTED && !alreadySolved) {
            pointsAwarded = contestProblem.getPoints();
        }

        ContestSubmission submission = new ContestSubmission();
        submission.setContest(contest);
        submission.setContestProblem(contestProblem);
        submission.setUser(user);
        submission.setSourceCode(sourceCode);
        submission.setStatus(result.getStatus());
        submission.setPassedTestCases(result.getPassedTestCases());
        submission.setTotalTestCases(result.getTotalTestCases());
        submission.setFirstFailedTestCaseOrder(result.getFirstFailedTestCaseOrder());
        submission.setStdout(result.getStdout());
        submission.setStderr(result.getStderr());
        submission.setExecutionTimeMs(result.getExecutionTimeMs());
        submission.setPointsAwarded(pointsAwarded);
        contestSubmissionRepository.save(submission);

        return toResponse(submission);
    }

    // Bu şagirdin bu yarış məsələsinə göndərdiyi bütün cəhdlərin tarixçəsi.
    @Transactional(readOnly = true)
    public List<ContestSubmissionResponse> history(Long contestProblemId, User user) {
        ContestProblem contestProblem = contestProblemRepository.findById(contestProblemId)
            .orElseThrow(() -> new NotFoundException("Yarış məsələsi tapılmadı: " + contestProblemId));
        List<ContestSubmission> submissions =
            contestSubmissionRepository.findByUserAndContestProblemOrderBySubmittedAtDesc(user, contestProblem);
        List<ContestSubmissionResponse> result = new ArrayList<>();
        for (ContestSubmission s : submissions) {
            result.add(toResponse(s));
        }
        return result;
    }

    // ContestSubmission entity-sini API-ya göstərilən ContestSubmissionResponse DTO-suna çevirən köməkçi metod.
    private ContestSubmissionResponse toResponse(ContestSubmission s) {
        return new ContestSubmissionResponse(
            s.getId(), s.getContestProblem().getId(), s.getSourceCode(), s.getStatus(),
            s.getPassedTestCases(), s.getTotalTestCases(), s.getFirstFailedTestCaseOrder(),
            s.getStdout(), s.getStderr(), s.getExecutionTimeMs(), s.getPointsAwarded(), s.getSubmittedAt()
        );
    }
}
