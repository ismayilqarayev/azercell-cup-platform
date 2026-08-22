package az.azcup.backend.controller;

import az.azcup.backend.dto.contest.ContestDto;
import az.azcup.backend.dto.contest.ContestManageDetailDto;
import az.azcup.backend.dto.contest.ContestProblemAdminDto;
import az.azcup.backend.dto.contest.ContestProblemUpsertRequest;
import az.azcup.backend.dto.contest.ContestTestCaseDto;
import az.azcup.backend.dto.contest.ContestTestCaseUpsertRequest;
import az.azcup.backend.dto.contest.ContestUpsertRequest;
import az.azcup.backend.service.ContestAdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// "/api/teacher/contests" prefiksi altında — SecurityConfig-in mövcud
// "/api/teacher/**" -> hasAnyRole("TEACHER","ADMIN") qaydası bu bütün yolu
// artıq əhatə edir, ƏLAVƏ konfiqurasiyaya EHTİYAC YOXDUR. Yarışın idarəetmə
// (yaratma/redaktə/məsələ+test halı əlavə etmə) tərəfi — şagird tərəfi
// ContestController-dədir.
@RestController
@RequestMapping("/api/teacher/contests")
public class ContestManageController {

    // Bütün faktiki idarəetmə əməliyyatlarını yerinə yetirən servis.
    private final ContestAdminService contestAdminService;

    // Spring tərəfindən inject olunan ContestAdminService-i sahəyə təyin edir.
    public ContestManageController(ContestAdminService contestAdminService) {
        this.contestAdminService = contestAdminService;
    }

    // ---------- Yarış (Contest) CRUD ----------

    // İdarəetmə panelində göstərmək üçün BÜTÜN yarışların siyahısı.
    @GetMapping
    public List<ContestDto> list() {
        return contestAdminService.listForManagement();
    }

    // Yeni yarış yaradır və 201 CREATED statusu ilə qaytarır.
    @PostMapping
    public ResponseEntity<ContestManageDetailDto> create(@Valid @RequestBody ContestUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contestAdminService.createContest(request));
    }

    // Bir yarışın tam detalları (bütün məsələləri, bütün test halları ilə).
    @GetMapping("/{id}")
    public ContestManageDetailDto get(@PathVariable Long id) {
        return contestAdminService.getManageDetail(id);
    }

    // Mövcud yarışı yeniləyir.
    @PutMapping("/{id}")
    public ContestManageDetailDto update(@PathVariable Long id, @Valid @RequestBody ContestUpsertRequest request) {
        return contestAdminService.updateContest(id, request);
    }

    // Bir yarışı silir.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        contestAdminService.deleteContest(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Yarış Məsələsi (ContestProblem) CRUD ----------

    // Yarışa yeni məsələ əlavə edir.
    @PostMapping("/{contestId}/problems")
    public ResponseEntity<ContestProblemAdminDto> addProblem(
        @PathVariable Long contestId,
        @Valid @RequestBody ContestProblemUpsertRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contestAdminService.addProblem(contestId, request));
    }

    // Mövcud yarış məsələsini yeniləyir.
    @PutMapping("/{contestId}/problems/{problemId}")
    public ContestProblemAdminDto updateProblem(
        @PathVariable Long contestId,
        @PathVariable Long problemId,
        @Valid @RequestBody ContestProblemUpsertRequest request
    ) {
        return contestAdminService.updateProblem(contestId, problemId, request);
    }

    // Bir yarış məsələsini (test halları ilə birlikdə) silir.
    @DeleteMapping("/{contestId}/problems/{problemId}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long contestId, @PathVariable Long problemId) {
        contestAdminService.deleteProblem(contestId, problemId);
        return ResponseEntity.noContent().build();
    }

    // ---------- Test Halı (ContestTestCase) CRUD ----------

    // Bir məsələyə yeni test halı əlavə edir.
    @PostMapping("/{contestId}/problems/{problemId}/test-cases")
    public ResponseEntity<ContestTestCaseDto> addTestCase(
        @PathVariable Long contestId,
        @PathVariable Long problemId,
        @Valid @RequestBody ContestTestCaseUpsertRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(contestAdminService.addTestCase(contestId, problemId, request));
    }

    // Mövcud test halını yeniləyir.
    @PutMapping("/{contestId}/problems/{problemId}/test-cases/{testCaseId}")
    public ContestTestCaseDto updateTestCase(
        @PathVariable Long contestId,
        @PathVariable Long problemId,
        @PathVariable Long testCaseId,
        @Valid @RequestBody ContestTestCaseUpsertRequest request
    ) {
        return contestAdminService.updateTestCase(contestId, problemId, testCaseId, request);
    }

    // Bir test halını silir.
    @DeleteMapping("/{contestId}/problems/{problemId}/test-cases/{testCaseId}")
    public ResponseEntity<Void> deleteTestCase(
        @PathVariable Long contestId,
        @PathVariable Long problemId,
        @PathVariable Long testCaseId
    ) {
        contestAdminService.deleteTestCase(contestId, problemId, testCaseId);
        return ResponseEntity.noContent().build();
    }
}
