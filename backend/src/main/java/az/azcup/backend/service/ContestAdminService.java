package az.azcup.backend.service;

import az.azcup.backend.dto.contest.ContestDto;
import az.azcup.backend.dto.contest.ContestManageDetailDto;
import az.azcup.backend.dto.contest.ContestProblemAdminDto;
import az.azcup.backend.dto.contest.ContestProblemUpsertRequest;
import az.azcup.backend.dto.contest.ContestTestCaseDto;
import az.azcup.backend.dto.contest.ContestTestCaseUpsertRequest;
import az.azcup.backend.dto.contest.ContestUpsertRequest;
import az.azcup.backend.entity.Contest;
import az.azcup.backend.entity.ContestProblem;
import az.azcup.backend.entity.ContestTestCase;
import az.azcup.backend.exception.NotFoundException;
import az.azcup.backend.repository.ContestProblemRepository;
import az.azcup.backend.repository.ContestRepository;
import az.azcup.backend.repository.ContestTestCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

// Yarış idarəetmə panelinin (Müəllim + Admin — hər ikisi eyni səlahiyyətə
// malikdir) bütün biznes-məntiqi: yarış/məsələ/test halı CRUD-u. AdminService-in
// Topic/Problem CRUD-u ilə eyni struktura görə qurulub (bax: createTopic
// və s.) — sadə "tap, sahələri təyin et, saxla" prinsipi.
@Service
public class ContestAdminService {

    // Yarışların CRUD əməliyyatları üçün.
    private final ContestRepository contestRepository;
    // Yarış məsələlərinin CRUD əməliyyatları üçün.
    private final ContestProblemRepository contestProblemRepository;
    // Test hallarının CRUD əməliyyatları üçün.
    private final ContestTestCaseRepository contestTestCaseRepository;

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public ContestAdminService(
        ContestRepository contestRepository,
        ContestProblemRepository contestProblemRepository,
        ContestTestCaseRepository contestTestCaseRepository
    ) {
        this.contestRepository = contestRepository;
        this.contestProblemRepository = contestProblemRepository;
        this.contestTestCaseRepository = contestTestCaseRepository;
    }

    // İdarəetmə panelində göstərmək üçün BÜTÜN yarışların siyahısı (status
    // fərq etmir — UPCOMING/ACTIVE/ENDED, hamısı görünür).
    @Transactional(readOnly = true)
    public List<ContestDto> listForManagement() {
        List<Contest> contests = contestRepository.findAllByOrderByStartTimeDesc();
        List<ContestDto> result = new ArrayList<>();
        for (Contest c : contests) {
            long problemCount = contestProblemRepository.countByContest(c);
            result.add(new ContestDto(
                c.getId(), c.getTitle(), c.getDescription(), c.getStartTime(), c.getEndTime(),
                ContestService.computeStatus(c), (int) problemCount
            ));
        }
        return result;
    }

    // Yeni yarış yaradır.
    @Transactional
    public ContestManageDetailDto createContest(ContestUpsertRequest req) {
        Contest contest = new Contest();
        applyUpsert(contest, req);
        contestRepository.save(contest);
        return toManageDetailDto(contest);
    }

    // Mövcud yarışın başlıq/təsvir/vaxtlarını yeniləyir.
    @Transactional
    public ContestManageDetailDto updateContest(Long contestId, ContestUpsertRequest req) {
        Contest contest = getContestEntity(contestId);
        applyUpsert(contest, req);
        contestRepository.save(contest);
        return toManageDetailDto(contest);
    }

    // Bir yarışı (və onun bütün məsələ/test halı/iştirakçı/təqdimat
    // sətirlərini, cascade vasitəsilə YOX — bax aşağı) silir. DİQQƏT: bu
    // sadə versiyada əlaqəli sətirləri ƏL İLƏ silmirik, çünki FK-lar
    // ON DELETE CASCADE ilə TƏYİN OLUNMAYIB (Hibernate auto-DDL default-u).
    // Ona görə silinmədən əvvəl məsələ olmayan (hələ heç kim qoşulmayan)
    // yarışları silmək tövsiyə olunur — məsələləri olan yarışı silmək FK
    // constraint xətası ilə uğursuz olacaq, bu da YANLIŞLIQLA "canlı"
    // (artıq iştirakçısı/nəticəsi olan) yarışın silinməsinin qarşısını alır.
    @Transactional
    public void deleteContest(Long contestId) {
        Contest contest = getContestEntity(contestId);
        contestRepository.delete(contest);
    }

    // İdarəetmə panelində bir yarışın tam detalları — BÜTÜN test halları
    // (gizli daxil) ilə birlikdə.
    @Transactional(readOnly = true)
    public ContestManageDetailDto getManageDetail(Long contestId) {
        Contest contest = getContestEntity(contestId);
        return toManageDetailDto(contest);
    }

    // Yarışa yeni məsələ əlavə edir.
    @Transactional
    public ContestProblemAdminDto addProblem(Long contestId, ContestProblemUpsertRequest req) {
        Contest contest = getContestEntity(contestId);
        ContestProblem problem = new ContestProblem();
        problem.setContest(contest);
        applyProblemUpsert(problem, req);
        contestProblemRepository.save(problem);
        return toProblemAdminDto(problem);
    }

    // Mövcud yarış məsələsini yeniləyir.
    @Transactional
    public ContestProblemAdminDto updateProblem(Long contestId, Long problemId, ContestProblemUpsertRequest req) {
        ContestProblem problem = getProblemEntity(contestId, problemId);
        applyProblemUpsert(problem, req);
        contestProblemRepository.save(problem);
        return toProblemAdminDto(problem);
    }

    // Bir yarış məsələsini (və onun test hallarını) silir.
    @Transactional
    public void deleteProblem(Long contestId, Long problemId) {
        ContestProblem problem = getProblemEntity(contestId, problemId);
        // Test halları əvvəlcə əl ilə silinir — FK ON DELETE CASCADE
        // təyin olunmadığı üçün, əks halda constraint xətası baş verərdi.
        List<ContestTestCase> testCases = contestTestCaseRepository.findByContestProblemOrderByOrderIndexAsc(problem);
        contestTestCaseRepository.deleteAll(testCases);
        contestProblemRepository.delete(problem);
    }

    // Bir məsələyə yeni test halı əlavə edir.
    @Transactional
    public ContestTestCaseDto addTestCase(Long contestId, Long problemId, ContestTestCaseUpsertRequest req) {
        ContestProblem problem = getProblemEntity(contestId, problemId);
        ContestTestCase testCase = new ContestTestCase();
        testCase.setContestProblem(problem);
        applyTestCaseUpsert(testCase, req);
        contestTestCaseRepository.save(testCase);
        return toTestCaseDto(testCase);
    }

    // Mövcud test halını yeniləyir.
    @Transactional
    public ContestTestCaseDto updateTestCase(Long contestId, Long problemId, Long testCaseId, ContestTestCaseUpsertRequest req) {
        ContestProblem problem = getProblemEntity(contestId, problemId);
        ContestTestCase testCase = contestTestCaseRepository.findById(testCaseId)
            .filter(tc -> tc.getContestProblem().getId().equals(problem.getId()))
            .orElseThrow(() -> new NotFoundException("Test halı tapılmadı: " + testCaseId));
        applyTestCaseUpsert(testCase, req);
        contestTestCaseRepository.save(testCase);
        return toTestCaseDto(testCase);
    }

    // Bir test halını silir.
    @Transactional
    public void deleteTestCase(Long contestId, Long problemId, Long testCaseId) {
        ContestProblem problem = getProblemEntity(contestId, problemId);
        ContestTestCase testCase = contestTestCaseRepository.findById(testCaseId)
            .filter(tc -> tc.getContestProblem().getId().equals(problem.getId()))
            .orElseThrow(() -> new NotFoundException("Test halı tapılmadı: " + testCaseId));
        contestTestCaseRepository.delete(testCase);
    }

    // ID ilə xam Contest entity-sini tapır, tapılmazsa 404 atır.
    private Contest getContestEntity(Long contestId) {
        return contestRepository.findById(contestId)
            .orElseThrow(() -> new NotFoundException("Yarış tapılmadı: " + contestId));
    }

    // ID ilə xam ContestProblem entity-sini tapır, HƏM DƏ onun DOĞRUDAN
    // bu yarışa aid olduğunu yoxlayır — əks halda başqa yarışın məsələ
    // ID-sini bu yarışın URL-inə yazaraq yanlış qovluqda redaktə etmək
    // mümkün olardı.
    private ContestProblem getProblemEntity(Long contestId, Long problemId) {
        ContestProblem problem = contestProblemRepository.findById(problemId)
            .orElseThrow(() -> new NotFoundException("Yarış məsələsi tapılmadı: " + problemId));
        if (!problem.getContest().getId().equals(contestId)) {
            throw new NotFoundException("Yarış məsələsi tapılmadı: " + problemId);
        }
        return problem;
    }

    // ContestUpsertRequest-dəki sahələri Contest entity-sinə köçürür.
    private void applyUpsert(Contest contest, ContestUpsertRequest req) {
        contest.setTitle(req.getTitle());
        contest.setDescription(req.getDescription());
        contest.setStartTime(req.getStartTime());
        contest.setEndTime(req.getEndTime());
    }

    // ContestProblemUpsertRequest-dəki sahələri ContestProblem entity-sinə köçürür.
    private void applyProblemUpsert(ContestProblem problem, ContestProblemUpsertRequest req) {
        problem.setOrderIndex(req.getOrderIndex());
        problem.setTitle(req.getTitle());
        problem.setStatement(req.getStatement());
        problem.setInputSpec(req.getInputSpec());
        problem.setOutputSpec(req.getOutputSpec());
        problem.setPoints(req.getPoints());
    }

    // ContestTestCaseUpsertRequest-dəki sahələri ContestTestCase entity-sinə köçürür.
    private void applyTestCaseUpsert(ContestTestCase testCase, ContestTestCaseUpsertRequest req) {
        testCase.setOrderIndex(req.getOrderIndex());
        // input boş göndərilə bilər (bəzi məsələlərdə giriş yoxdur) — null-u boş sətirə çeviririk.
        if (req.getInput() != null) {
            testCase.setInput(req.getInput());
        } else {
            testCase.setInput("");
        }
        testCase.setExpectedOutput(req.getExpectedOutput());
        testCase.setHidden(req.getHidden());
    }

    // Contest entity-sini (bütün məsələləri, hər məsələnin bütün test
    // halları ilə birlikdə) ContestManageDetailDto-ya çevirir.
    private ContestManageDetailDto toManageDetailDto(Contest contest) {
        List<ContestProblemAdminDto> problems = new ArrayList<>();
        for (ContestProblem cp : contestProblemRepository.findByContestOrderByOrderIndexAsc(contest)) {
            problems.add(toProblemAdminDto(cp));
        }
        return new ContestManageDetailDto(
            contest.getId(), contest.getTitle(), contest.getDescription(),
            contest.getStartTime(), contest.getEndTime(), ContestService.computeStatus(contest), problems
        );
    }

    // ContestProblem entity-sini, BÜTÜN test halları ilə birlikdə,
    // ContestProblemAdminDto-ya çevirir.
    private ContestProblemAdminDto toProblemAdminDto(ContestProblem problem) {
        List<ContestTestCaseDto> testCases = new ArrayList<>();
        for (ContestTestCase tc : contestTestCaseRepository.findByContestProblemOrderByOrderIndexAsc(problem)) {
            testCases.add(toTestCaseDto(tc));
        }
        return new ContestProblemAdminDto(
            problem.getId(), problem.getOrderIndex(), problem.getTitle(), problem.getStatement(),
            problem.getInputSpec(), problem.getOutputSpec(), problem.getPoints(), testCases
        );
    }

    // ContestTestCase entity-sini ContestTestCaseDto-ya çevirir.
    private ContestTestCaseDto toTestCaseDto(ContestTestCase testCase) {
        return new ContestTestCaseDto(
            testCase.getId(), testCase.getOrderIndex(), testCase.getInput(), testCase.getExpectedOutput(), testCase.isHidden()
        );
    }
}
