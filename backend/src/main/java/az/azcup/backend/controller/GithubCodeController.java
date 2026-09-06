package az.azcup.backend.controller;

import az.azcup.backend.dto.github.GithubCodeTreeResponse;
import az.azcup.backend.service.GithubCodeSyncService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Müəllimin öz GitHub repo-sundakı (ismayilqarayev/eolymp, cpp/kodlar
// qovluğu) kod nümunələrini "Kod Arxivi" tabında göstərmək üçün — bax:
// GithubCodeSyncService (arxa planda hər 5 dəqiqədən bir avtomatik
// sinxronlaşır). Hər iki endpoint giriş etmiş İSTƏNİLƏN rol üçün açıqdır
// (bax: SecurityConfig-dəki ümumi "/api/**" -> authenticated() qaydası).
@RestController
@RequestMapping("/api/github-codes")
public class GithubCodeController {

    private final GithubCodeSyncService githubCodeSyncService;

    public GithubCodeController(GithubCodeSyncService githubCodeSyncService) {
        this.githubCodeSyncService = githubCodeSyncService;
    }

    // Hazırkı (keşlənmiş) kod ağacını qaytarır.
    @GetMapping
    public GithubCodeTreeResponse getTree() {
        return githubCodeSyncService.getTree();
    }

    // Planlaşdırılmış 5 dəqiqəni gözləmədən dərhal yeniləmək üçün. Minimum
    // 20 saniyəlik məsafə pozulubsa 429 qaytarır (yenə də hazırkı keşi ötürür).
    @PostMapping("/refresh")
    public ResponseEntity<GithubCodeTreeResponse> refresh() {
        boolean refreshed = githubCodeSyncService.forceRefresh();
        if (!refreshed) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(githubCodeSyncService.getTree());
        }
        return ResponseEntity.ok(githubCodeSyncService.getTree());
    }
}
