package az.azcup.backend.controller;

import az.azcup.backend.dto.admin.AdminTopicDto;
import az.azcup.backend.dto.admin.PublishUpdateRequest;
import az.azcup.backend.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * "/api/teacher" prefiksi altında müəllimin öz panelindən idarə edə biləcəyi
 * əməliyyatlar. Hazırda tək bir imkan var: mövzuların (dərs proqramının)
 * tələbəyə açıq olub-olmadığını dəyişmək. SecurityConfig-də bu yollar həm
 * TEACHER, həm də ADMIN roluna açılıb — mövzu hansısa konkret müəllimə aid
 * olmadığından (sxemdə belə bir əlaqə yoxdur), istənilən müəllim istənilən
 * mövzunu aça/bağlaya bilər.
 */
@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    // Mövzu idarəetməsi məntiqi AdminService-də mərkəzləşdirilib, bu controller onu yenidən istifadə edir.
    private final AdminService adminService;

    // Spring tərəfindən inject olunan AdminService-i sahəyə təyin edir.
    public TeacherController(AdminService adminService) {
        this.adminService = adminService;
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
}
