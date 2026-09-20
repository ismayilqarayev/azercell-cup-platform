package az.azcup.backend.controller;

import az.azcup.backend.dto.ActivityDayDto;
import az.azcup.backend.dto.admin.AdminTopicDto;
import az.azcup.backend.dto.admin.PublishUpdateRequest;
import az.azcup.backend.dto.teacher.AddGroupMemberRequest;
import az.azcup.backend.dto.ExampleCheckRequest;
import az.azcup.backend.dto.ExampleCheckResponse;
import az.azcup.backend.dto.teacher.AssignmentDto;
import az.azcup.backend.dto.teacher.AssignmentUpsertRequest;
import az.azcup.backend.dto.teacher.ExampleDto;
import az.azcup.backend.dto.teacher.ExampleProgressRowDto;
import az.azcup.backend.dto.teacher.ExampleUpsertRequest;
import az.azcup.backend.dto.teacher.GradebookRowDto;
import az.azcup.backend.dto.teacher.StudentExampleDetailDto;
import az.azcup.backend.dto.teacher.GroupCreateRequest;
import az.azcup.backend.dto.teacher.GroupDto;
import az.azcup.backend.dto.teacher.GroupMemberDto;
import az.azcup.backend.security.UserPrincipal;
import az.azcup.backend.service.AdminService;
import az.azcup.backend.service.AssignmentService;
import az.azcup.backend.service.GroupService;
import az.azcup.backend.service.SubmissionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * "/api/teacher" prefiksi altında müəllimin öz panelindən idarə edə biləcəyi
 * əməliyyatlar: mövzuların (dərs proqramının) tələbəyə açıq olub-olmadığını
 * dəyişmək, və öz şagird qruplarını idarə etmək. SecurityConfig-də bu yollar
 * həm TEACHER, həm də ADMIN roluna açılıb. Mövzu idarəetməsi hər müəllimə
 * açıqdır (sxemdə mövzu-müəllim əlaqəsi yoxdur), amma qrup əməliyyatları
 * GroupService daxilində sahiblik yoxlaması ilə qorunur (bax:
 * GroupService.requireOwnership) — bir müəllim yalnız ÖZ qrupunu görə/dəyişə bilər.
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    // Mövzu idarəetməsi məntiqi AdminService-də mərkəzləşdirilib, bu controller onu yenidən istifadə edir.
    private final AdminService adminService;
    // Qrup CRUD-u və üzvlük idarəetməsi məntiqi GroupService-dədir.
    private final GroupService groupService;
    // Tapşırıq CRUD-u və sinif jurnalı (gradebook) məntiqi AssignmentService-dədir.
    private final AssignmentService assignmentService;
    // Şagirdin fəaliyyət xəritəsini (bax: /students/{id}/activity) gətirmək üçün.
    private final SubmissionService submissionService;

    // Spring tərəfindən inject olunan asılılıqları sahələrə təyin edir.
    public TeacherController(
        AdminService adminService,
        GroupService groupService,
        AssignmentService assignmentService,
        SubmissionService submissionService
    ) {
        this.adminService = adminService;
        this.groupService = groupService;
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
    }

    // Müəllim panelində idarəetmə üçün bütün mövzuların (dərc statusundan asılı olmayaraq) siyahısını qaytarır.
    @GetMapping("/topics")
    public List<AdminTopicDto> listTopics() {
        return adminService.listTopicsForManagement();
    }

    // Mövzunun dərc (published) statusunu dəyişir.
    @PutMapping("/topics/{id}/publish")
    public AdminTopicDto setPublished(@PathVariable Long id, @RequestBody PublishUpdateRequest request) {
        return adminService.setTopicPublished(id, request.isPublished());
    }

    // ---------- Qrup (Group) idarəetməsi ----------

    // Sorğunu edən müəllimin sahib olduğu bütün qrupların siyahısı.
    @GetMapping("/groups")
    public List<GroupDto> listMyGroups(@AuthenticationPrincipal UserPrincipal principal) {
        return groupService.listMyGroups(principal.getUser());
    }

    // Yeni qrup yaradır, sahibi sorğunu edən müəllim olur.
    @PostMapping("/groups")
    public ResponseEntity<GroupDto> createGroup(
        @Valid @RequestBody GroupCreateRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        GroupDto created = groupService.createGroup(principal.getUser(), request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Qrupun adını dəyişir.
    @PutMapping("/groups/{id}")
    public GroupDto renameGroup(
        @PathVariable Long id,
        @Valid @RequestBody GroupCreateRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return groupService.renameGroup(id, principal.getUser(), request.getName());
    }

    // Qrupu silir.
    @DeleteMapping("/groups/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        groupService.deleteGroup(id, principal.getUser());
        return ResponseEntity.noContent().build();
    }

    // Qrupun üzv (şagird) siyahısını, hər birinin irəliləyişi ilə birlikdə qaytarır.
    @GetMapping("/groups/{id}/students")
    public List<GroupMemberDto> listGroupMembers(
        @PathVariable Long id,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return groupService.listMembers(id, principal.getUser());
    }

    // E-poçtu göndərilən şagirdi qrupa əlavə edir.
    @PostMapping("/groups/{id}/students")
    public ResponseEntity<GroupMemberDto> addGroupMember(
        @PathVariable Long id,
        @Valid @RequestBody AddGroupMemberRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        GroupMemberDto added = groupService.addStudent(id, principal.getUser(), request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(added);
    }

    // Şagirdi qrupdan çıxarır (membershipId — üzvlük sətrinin ID-si).
    @DeleteMapping("/groups/{id}/students/{membershipId}")
    public ResponseEntity<Void> removeGroupMember(
        @PathVariable Long id,
        @PathVariable Long membershipId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        groupService.removeStudent(id, principal.getUser(), membershipId);
        return ResponseEntity.noContent().build();
    }

    // Bir şagirdin fəaliyyət xəritəsi ("Şagirdlər" panelində və qrup
    // detalında hər üzvün yanında göstərilir) — bax: SubmissionService.activityForUser.
    // Sahiblik yoxlaması yoxdur: "Şagirdlər" siyahısının özü artıq istənilən
    // TEACHER/ADMIN-ə açıqdır (bax: SecurityConfig-dəki GET /api/admin/users qaydası).
    @GetMapping("/students/{id}/activity")
    public List<ActivityDayDto> studentActivity(@PathVariable Long id) {
        return submissionService.activityForUser(id);
    }

    // ---------- Tapşırıq (Assignment) idarəetməsi ----------

    // Qrupun bütün tapşırıqlarının siyahısı.
    @GetMapping("/groups/{groupId}/assignments")
    public List<AssignmentDto> listAssignments(
        @PathVariable Long groupId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return assignmentService.listForGroup(groupId, principal.getUser());
    }

    // Yeni tapşırıq yaradır.
    @PostMapping("/groups/{groupId}/assignments")
    public ResponseEntity<AssignmentDto> createAssignment(
        @PathVariable Long groupId,
        @Valid @RequestBody AssignmentUpsertRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        AssignmentDto created = assignmentService.create(groupId, principal.getUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Mövcud tapşırığı yeniləyir.
    @PutMapping("/groups/{groupId}/assignments/{assignmentId}")
    public AssignmentDto updateAssignment(
        @PathVariable Long groupId,
        @PathVariable Long assignmentId,
        @Valid @RequestBody AssignmentUpsertRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return assignmentService.update(groupId, assignmentId, principal.getUser(), request);
    }

    // Tapşırığı silir.
    @DeleteMapping("/groups/{groupId}/assignments/{assignmentId}")
    public ResponseEntity<Void> deleteAssignment(
        @PathVariable Long groupId,
        @PathVariable Long assignmentId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        assignmentService.delete(groupId, assignmentId, principal.getUser());
        return ResponseEntity.noContent().build();
    }

    // Bir tapşırığın sinif jurnalı (gradebook) — qrupun hər üzvünün irəliləyişi.
    @GetMapping("/groups/{groupId}/assignments/{assignmentId}/gradebook")
    public List<GradebookRowDto> getGradebook(
        @PathVariable Long groupId,
        @PathVariable Long assignmentId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return assignmentService.getGradebook(groupId, assignmentId, principal.getUser());
    }

    // ---------- Tapşırığın kod nümunələri (şagird baxıb yazır) ----------

    // Tapşırığın kod nümunələri, hər birini neçə şagirdin tamamladığı ilə.
    @GetMapping("/groups/{groupId}/assignments/{assignmentId}/examples")
    public List<ExampleDto> listExamples(
        @PathVariable Long groupId,
        @PathVariable Long assignmentId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return assignmentService.listExamples(groupId, assignmentId, principal.getUser());
    }

    // Tapşırığa yeni kod nümunəsi əlavə edir.
    @PostMapping("/groups/{groupId}/assignments/{assignmentId}/examples")
    public ResponseEntity<ExampleDto> addExample(
        @PathVariable Long groupId,
        @PathVariable Long assignmentId,
        @Valid @RequestBody ExampleUpsertRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        ExampleDto created = assignmentService.addExample(groupId, assignmentId, principal.getUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Kod nümunəsini silir.
    @DeleteMapping("/groups/{groupId}/assignments/{assignmentId}/examples/{exampleId}")
    public ResponseEntity<Void> deleteExample(
        @PathVariable Long groupId,
        @PathVariable Long assignmentId,
        @PathVariable Long exampleId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        assignmentService.deleteExample(groupId, assignmentId, exampleId, principal.getUser());
        return ResponseEntity.noContent().build();
    }

    // Bir şagirdin bu tapşırığın hər nümunəsi üzrə irəliləyişi (tamamlanma vaxtı, cəhdlər, son yazdığı kod).
    @GetMapping("/groups/{groupId}/assignments/{assignmentId}/students/{studentId}/examples")
    public List<StudentExampleDetailDto> getStudentExampleDetails(
        @PathVariable Long groupId,
        @PathVariable Long assignmentId,
        @PathVariable Long studentId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return assignmentService.getStudentExampleDetails(groupId, assignmentId, studentId, principal.getUser());
    }

    // Müəllimin "şagird kimi bax" baxışı üçün: yazılanı nümunə ilə müqayisə edir, tamamlanma QEYD ETMİR.
    @PostMapping("/groups/{groupId}/assignments/{assignmentId}/examples/{exampleId}/check")
    public ExampleCheckResponse previewCheckExample(
        @PathVariable Long groupId,
        @PathVariable Long assignmentId,
        @PathVariable Long exampleId,
        @Valid @RequestBody ExampleCheckRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return assignmentService.previewCheckExample(groupId, assignmentId, exampleId, principal.getUser(), request.getTypedCode());
    }

    // Qrupun hər şagirdinin nümunələrdən neçəsini tamamladığı (jurnal üçün).
    @GetMapping("/groups/{groupId}/assignments/{assignmentId}/examples/progress")
    public List<ExampleProgressRowDto> getExampleProgress(
        @PathVariable Long groupId,
        @PathVariable Long assignmentId,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return assignmentService.getExampleProgress(groupId, assignmentId, principal.getUser());
    }
}
