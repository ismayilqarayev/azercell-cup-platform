package az.azcup.backend.service;

import az.azcup.backend.dto.contest.ContestDetailDto;
import az.azcup.backend.dto.contest.ContestDto;
import az.azcup.backend.dto.contest.ContestProblemDto;
import az.azcup.backend.dto.contest.ContestStatusEnum;
import az.azcup.backend.dto.contest.ContestTestCaseDto;
import az.azcup.backend.dto.contest.LeaderboardEntryDto;
import az.azcup.backend.entity.Contest;
import az.azcup.backend.entity.ContestParticipant;
import az.azcup.backend.entity.ContestProblem;
import az.azcup.backend.entity.ContestSubmission;
import az.azcup.backend.entity.ContestTestCase;
import az.azcup.backend.entity.Role;
import az.azcup.backend.entity.User;
import az.azcup.backend.exception.ApiException;
import az.azcup.backend.exception.ConflictException;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.repository.ContestParticipantRepository;
import az.azcup.backend.repository.ContestProblemRepository;
import az.azcup.backend.repository.ContestRepository;
import az.azcup.backend.repository.ContestSubmissionRepository;
import az.azcup.backend.repository.ContestTestCaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Şagirdin (və ümumiyyətlə istənilən girişli istifadəçinin) yarışlara BAXMASI,
// QOŞULMASI və REYTİNQ CƏDVƏLİNƏ baxması ilə bağlı biznes-məntiq. Yaratma/
// redaktə ContestAdminService-dədir, göndərmə/yoxlama ContestSubmissionService-dədir —
// eyni üç-hissəli bölgü ProblemService/AdminService/SubmissionService-də
// olduğu kimi.
@Service
public class ContestService {

    // Yarışları oxumaq üçün.
    private final ContestRepository contestRepository;
    // Yarış məsələlərini oxumaq üçün.
    private final ContestProblemRepository contestProblemRepository;
    // Test hallarını oxumaq üçün (yalnız nümunə olanlar şagirdə göstərilir).
    private final ContestTestCaseRepository contestTestCaseRepository;
    // İştirakçı qeydlərini oxumaq/yazmaq üçün (join, "artıq qoşulub mu" yoxlaması).
    private final ContestParticipantRepository contestParticipantRepository;
    // Reytinq cədvəlini hesablamaq üçün bütün təqdimatları oxumaq lazımdır.
    private final ContestSubmissionRepository contestSubmissionRepository;

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public ContestService(
        ContestRepository contestRepository,
        ContestProblemRepository contestProblemRepository,
        ContestTestCaseRepository contestTestCaseRepository,
        ContestParticipantRepository contestParticipantRepository,
        ContestSubmissionRepository contestSubmissionRepository
    ) {
        this.contestRepository = contestRepository;
        this.contestProblemRepository = contestProblemRepository;
        this.contestTestCaseRepository = contestTestCaseRepository;
        this.contestParticipantRepository = contestParticipantRepository;
        this.contestSubmissionRepository = contestSubmissionRepository;
    }

    // Bir yarışın startTime/endTime-ına və CARİ vaxta görə statusunu
    // hesablayır. STATİK saxlanılır ki, ContestSubmissionService də (əsl
    // "yarış aktivdirmi" qadağasını server tərəfində tətbiq etmək üçün)
    // eyni məntiqi TƏKRARSIZ istifadə edə bilsin.
    public static ContestStatusEnum computeStatus(Contest contest) {
        Instant now = Instant.now();
        if (now.isBefore(contest.getStartTime())) {
            return ContestStatusEnum.UPCOMING;
        }
        if (now.isAfter(contest.getEndTime())) {
            return ContestStatusEnum.ENDED;
        }
        return ContestStatusEnum.ACTIVE;
    }

    // Bütün yarışların qısa siyahısı (status hər sorğuda təzədən hesablanır).
    @Transactional(readOnly = true)
    public List<ContestDto> list() {
        List<Contest> contests = contestRepository.findAllByOrderByStartTimeDesc();
        List<ContestDto> result = new ArrayList<>();
        for (Contest c : contests) {
            result.add(toDto(c));
        }
        return result;
    }

    // Tək bir yarışın tam detalları. Məsələlər YALNIZ yarış artıq
    // başlayıbsa (ACTIVE/ENDED) göstərilir — UPCOMING zamanı şagird nə
    // qədər məsələ olduğunu belə görmür, sadəcə başlıq/vaxt/təsviri görür.
    // Müəllim/admin isə həmişə (hələ başlamasa belə) görə bilir ki, tərtib
    // etdiyi sualları yoxlaya bilsin.
    @Transactional(readOnly = true)
    public ContestDetailDto getDetail(Long contestId, User user) {
        Contest contest = getEntity(contestId);
        ContestStatusEnum status = computeStatus(contest);

        boolean joined = false;
        if (user.getRole() == Role.STUDENT) {
            joined = contestParticipantRepository.findByContestAndUser(contest, user).isPresent();
        }

        List<ContestProblemDto> problems = new ArrayList<>();
        boolean showProblems = user.getRole() != Role.STUDENT || status != ContestStatusEnum.UPCOMING;
        if (showProblems) {
            for (ContestProblem cp : contestProblemRepository.findByContestOrderByOrderIndexAsc(contest)) {
                problems.add(toStudentProblemDto(cp, user));
            }
        }

        return new ContestDetailDto(
            contest.getId(), contest.getTitle(), contest.getDescription(),
            contest.getStartTime(), contest.getEndTime(), status, joined, problems
        );
    }

    // Şagirdi bir yarışa qoşur. Yalnız STUDENT rolu qoşula bilər (müəllim/
    // admin idarə edir, yarışmır) — bax: ContestSubmissionService-dəki
    // eyni rol yoxlaması. Artıq bitmiş yarışa qoşulmağın mənası yoxdur.
    @Transactional
    public void join(Long contestId, User user) {
        if (user.getRole() != Role.STUDENT) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Yalnız şagirdlər yarışa qoşula bilər");
        }
        Contest contest = getEntity(contestId);
        if (computeStatus(contest) == ContestStatusEnum.ENDED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Bu yarış artıq bitib, qoşulmaq mümkün deyil");
        }
        if (contestParticipantRepository.findByContestAndUser(contest, user).isPresent()) {
            throw new ConflictException("Siz artıq bu yarışa qoşulmusunuz");
        }
        ContestParticipant participant = new ContestParticipant();
        participant.setContest(contest);
        participant.setUser(user);
        contestParticipantRepository.save(participant);
    }

    // Bir yarışın reytinq cədvəli: CƏMİ BALA görə azalan, bərabərlikdə isə
    // həmin bala İLK ÇATDIĞI vaxta görə artan (kim tez çatıbsa öndə) sırada.
    // Mürəkkəb JPQL GROUP BY sorğusu əvəzinə hər şeyi Java-da hesablayırıq —
    // bu layihədə (bax: SubmissionService.progress) üstünlük verilən üsuldur.
    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> leaderboard(Long contestId) {
        Contest contest = getEntity(contestId);

        // Əvvəlcə hər iştirakçını 0 balla "başlanğıc sətri" kimi qeyd edirik ki,
        // hələ heç bir məsələni həll etməyənlər də cədvəldə görünsün.
        Map<Long, ScoreAccumulator> byUserId = new HashMap<>();
        for (ContestParticipant participant : contestParticipantRepository.findByContest(contest)) {
            User u = participant.getUser();
            byUserId.put(u.getId(), new ScoreAccumulator(u.getId(), u.getFullName()));
        }

        // Bütün təqdimatları vaxta görə ARTAN sırada gəzib, yalnız BAL
        // QAZANDIRAN (pointsAwarded > 0) cəhdləri toplayırıq — "hamısı ya
        // heç nə" siyasətinə görə hər məsələ üçün ən çoxu BİR belə cəhd var.
        for (ContestSubmission submission : contestSubmissionRepository.findByContestOrderBySubmittedAtAsc(contest)) {
            if (submission.getPointsAwarded() <= 0) {
                continue;
            }
            Long userId = submission.getUser().getId();
            ScoreAccumulator acc = byUserId.get(userId);
            if (acc == null) {
                // Nəzəri olaraq baş verməməlidir (submit üçün əvvəlcə qoşulmaq
                // şərtdir), amma qorunma xatirinə boş sətir yaradırıq.
                acc = new ScoreAccumulator(userId, submission.getUser().getFullName());
                byUserId.put(userId, acc);
            }
            acc.totalPoints += submission.getPointsAwarded();
            acc.solvedCount += 1;
            // submissions vaxta görə ARTAN sırada gəzildiyi üçün, sonuncu
            // yazılan dəyər avtomatik olaraq "bu bala çatdığı SON (indiyədək
            // ən son) vaxt" olur.
            acc.scoreReachedAt = submission.getSubmittedAt();
        }

        List<ScoreAccumulator> accumulators = new ArrayList<>(byUserId.values());
        // Cəmi bala görə AZALAN, bərabərlikdə isə scoreReachedAt-a görə
        // ARTAN (daha tez çatan öndə) sırada sıralayır. Hələ heç nə həll
        // etməyənlərin (scoreReachedAt == null) sırası əhəmiyyətsizdir,
        // ona görə onlar nullsLast ilə sona atılır.
        accumulators.sort(
            Comparator.<ScoreAccumulator>comparingInt(a -> -a.totalPoints)
                .thenComparing(a -> a.scoreReachedAt, Comparator.nullsLast(Comparator.naturalOrder()))
        );

        List<LeaderboardEntryDto> result = new ArrayList<>();
        int rank = 1;
        for (ScoreAccumulator acc : accumulators) {
            result.add(new LeaderboardEntryDto(rank, acc.userId, acc.fullName, acc.totalPoints, acc.solvedCount, acc.scoreReachedAt));
            rank++;
        }
        return result;
    }

    // ID ilə xam Contest entity-sini tapır — SubmissionService/AdminService
    // də daxil olmaqla, digər servislər bu metod vasitəsilə keçir.
    @Transactional(readOnly = true)
    public Contest getEntity(Long contestId) {
        return contestRepository.findById(contestId)
            .orElseThrow(() -> new NotFoundException("Yarış tapılmadı: " + contestId));
    }

    // Contest entity-sini siyahı görünüşü üçün ContestDto-ya çevirir.
    private ContestDto toDto(Contest contest) {
        long problemCount = contestProblemRepository.countByContest(contest);
        return new ContestDto(
            contest.getId(), contest.getTitle(), contest.getDescription(),
            contest.getStartTime(), contest.getEndTime(), computeStatus(contest), (int) problemCount
        );
    }

    // Bir yarış məsələsini, MÜƏYYƏN İSTİFADƏÇİ üçün "solved" bayrağı və
    // yalnız NÜMUNƏ (gizli olmayan) test halları ilə birlikdə
    // ContestProblemDto-ya çevirir — gizli test halları bu metoddan HEÇ
    // VAXT xaricə sızmır.
    private ContestProblemDto toStudentProblemDto(ContestProblem cp, User user) {
        List<ContestTestCaseDto> sampleTestCases = new ArrayList<>();
        for (ContestTestCase tc : contestTestCaseRepository.findByContestProblemOrderByOrderIndexAsc(cp)) {
            if (!tc.isHidden()) {
                sampleTestCases.add(new ContestTestCaseDto(tc.getId(), tc.getOrderIndex(), tc.getInput(), tc.getExpectedOutput(), false));
            }
        }
        boolean solved = user.getRole() == Role.STUDENT
            && contestSubmissionRepository.existsByUserAndContestProblemAndPointsAwardedGreaterThan(user, cp, 0);
        return new ContestProblemDto(
            cp.getId(), cp.getOrderIndex(), cp.getTitle(), cp.getStatement(),
            cp.getInputSpec(), cp.getOutputSpec(), cp.getPoints(), sampleTestCases, solved
        );
    }

    // leaderboard() daxilində hər iştirakçının balını toplamaq üçün
    // istifadə olunan, YALNIZ bu metodun daxilində yaşayan kiçik köməkçi
    // dəyişən "torbası" — ayrıca fayla çıxarmağa dəyməz qədər kiçikdir.
    private static final class ScoreAccumulator {
        private final Long userId;
        private final String fullName;
        private int totalPoints = 0;
        private int solvedCount = 0;
        private Instant scoreReachedAt = null;

        private ScoreAccumulator(Long userId, String fullName) {
            this.userId = userId;
            this.fullName = fullName;
        }
    }
}
