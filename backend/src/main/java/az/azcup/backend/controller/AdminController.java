package az.azcup.backend.controller;

import az.azcup.backend.dto.admin.AdminProblemDto;
import az.azcup.backend.dto.admin.AdminTopicDto;
import az.azcup.backend.dto.admin.AdminUserDetailDto;
import az.azcup.backend.dto.admin.AdminUserDto;
import az.azcup.backend.dto.admin.PasswordResetRequest;
import az.azcup.backend.dto.admin.PasswordResetResponse;
import az.azcup.backend.dto.admin.ProblemUpsertRequest;
import az.azcup.backend.dto.admin.RoleUpdateRequest;
import az.azcup.backend.dto.admin.StatusUpdateRequest;
import az.azcup.backend.dto.admin.TopicUpsertRequest;
import az.azcup.backend.dto.admin.UserProfileUpdateRequest;
import az.azcup.backend.security.UserPrincipal;
import az.azcup.backend.service.AdminService;
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

// "/api/admin" prefiksi altında — SecurityConfig bu bütün yolu (GET
// /api/admin/users istisna olmaqla, o həm TEACHER-ə açıqdır) yalnız ADMIN
// roluna açır. Faktiki iş məntiqi AdminService-dədir, burada sadəcə
// HTTP <-> DTO uyğunlaşdırması var.
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    // Bütün faktiki admin əməliyyatlarını (CRUD, istifadəçi idarəetməsi) yerinə yetirən servis.
    private final AdminService adminService;

    // Spring tərəfindən inject olunan AdminService-i sahəyə təyin edir.
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ---------- Mövzu (Topic) CRUD ----------

    // Yeni mövzu yaradır və 201 CREATED statusu ilə yaradılmış mövzunu qaytarır.
    @PostMapping("/topics")
    public ResponseEntity<AdminTopicDto> createTopic(@Valid @RequestBody TopicUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createTopic(request));
    }

    // Mövcud mövzunu ID-yə görə yeniləyir.
    @PutMapping("/topics/{id}")
    public AdminTopicDto updateTopic(@PathVariable Long id, @Valid @RequestBody TopicUpsertRequest request) {
        return adminService.updateTopic(id, request);
    }

    // Mövzunu ID-yə görə silir və 204 NO_CONTENT qaytarır.
    @DeleteMapping("/topics/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        adminService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Problem CRUD ----------

    // Yeni problem yaradır və 201 CREATED statusu ilə yaradılmış problemi qaytarır.
    @PostMapping("/problems")
    public ResponseEntity<AdminProblemDto> createProblem(@Valid @RequestBody ProblemUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createProblem(request));
    }

    // Mövcud problemi ID-yə görə yeniləyir.
    @PutMapping("/problems/{id}")
    public AdminProblemDto updateProblem(@PathVariable Long id, @Valid @RequestBody ProblemUpsertRequest request) {
        return adminService.updateProblem(id, request);
    }

    // Problemi ID-yə görə silir və 204 NO_CONTENT qaytarır.
    @DeleteMapping("/problems/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        adminService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- İstifadəçi idarəetməsi ----------

    // Bu endpoint həm ADMIN, həm TEACHER-ə açıqdır (bax: SecurityConfig) —
    // müəllim şagird siyahısını görə bilməlidir, amma dəyişiklik edə bilməz.
    @GetMapping("/users")
    public List<AdminUserDto> listStudents() {
        return adminService.listStudents();
    }

    // Hələ təsdiqlənməmiş (approved=false) müəllim hesablarının siyahısını qaytarır.
    @GetMapping("/pending-teachers")
    public List<AdminUserDto> listPendingTeachers() {
        return adminService.listPendingTeachers();
    }

    // Müəllim hesabını təsdiqləyir (approved=true edir).
    @PostMapping("/teachers/{id}/approve")
    public ResponseEntity<Void> approveTeacher(@PathVariable Long id) {
        adminService.approveTeacher(id);
        return ResponseEntity.noContent().build();
    }

    // Təsdiq gözləyən müəllim hesabını rədd edir (silir).
    @DeleteMapping("/teachers/{id}")
    public ResponseEntity<Void> rejectTeacher(@PathVariable Long id) {
        adminService.rejectTeacher(id);
        return ResponseEntity.noContent().build();
    }

    // Rolundan asılı olmayaraq bütün istifadəçilərin tam siyahısını qaytarır.
    @GetMapping("/users/all")
    public List<AdminUserDetailDto> listAllUsers() {
        return adminService.listAllUsers();
    }

    // Tək bir istifadəçinin tam detallarını ID-yə görə qaytarır.
    @GetMapping("/users/{id}")
    public AdminUserDetailDto getUser(@PathVariable Long id) {
        return adminService.getUser(id);
    }

    // İstifadəçinin ad/e-poçt profilini yeniləyir.
    @PutMapping("/users/{id}")
    public AdminUserDetailDto updateUserProfile(
        @PathVariable Long id,
        @Valid @RequestBody UserProfileUpdateRequest request
    ) {
        return adminService.updateProfile(id, request);
    }

    // principal.getUser().getId() — bunu edən adminin öz ID-si AdminService-ə
    // ötürülür ki, "özünü admin olmayan rola dəyişmə" kimi hallar bloklana
    // bilsin (bax: AdminService.changeRole).
    @PutMapping("/users/{id}/role")
    public AdminUserDetailDto changeUserRole(
        @PathVariable Long id,
        @Valid @RequestBody RoleUpdateRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return adminService.changeRole(id, request.getRole(), principal.getUser().getId());
    }

    @PutMapping("/users/{id}/status")
    public AdminUserDetailDto changeUserStatus(
        @PathVariable Long id,
        @RequestBody StatusUpdateRequest request,
        @AuthenticationPrincipal UserPrincipal principal
    ) {
        return adminService.setActive(id, request.isActive(), principal.getUser().getId());
    }

    // request(required = false) — gövdə tamamilə boş göndərilə bilər
    // (admin sadəcə "təsadüfi parol yarat" istəyəndə).
    @PostMapping("/users/{id}/reset-password")
    public PasswordResetResponse resetUserPassword(
        @PathVariable Long id,
        @RequestBody(required = false) PasswordResetRequest request
    ) {
        String requestedPassword;
        if (request != null) {
            requestedPassword = request.getNewPassword();
        } else {
            requestedPassword = null;
        }
        String newPassword = adminService.resetPassword(id, requestedPassword);
        return new PasswordResetResponse(newPassword);
    }

    // İstifadəçini silir (özünü silməyə cəhd AdminService tərəfindən bloklanır).
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        adminService.deleteUser(id, principal.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}
