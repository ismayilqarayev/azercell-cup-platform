package az.azcup.backend.service;

import az.azcup.backend.dto.github.GithubCodeNodeDto;
import az.azcup.backend.dto.github.GithubCodeTreeResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// "Kod Arxivi" tabı üçün müəllimin öz GitHub repo-sundakı (ismayilqarayev/eolymp,
// cpp/kodlar qovluğu) kod nümunələrini davamlı oxuyub yaddaşda (in-memory)
// saxlayır — bax: LiveSessionState-dəki eyni "JPA-sız sadə keş" qərarı.
// Müəllim sadəcə Visual Studio Code-da yazıb GitHub-a push edir; platforma
// tərəfində HEÇ NƏ dəyişdirmədən bir neçə dəqiqə içində şagirdlərə görünür.
@Service
public class GithubCodeSyncService {

    private final Logger log = LoggerFactory.getLogger(GithubCodeSyncService.class);

    private static final String OWNER = "ismayilqarayev";
    private static final String REPO = "eolymp";
    private static final String BRANCH = "main";
    private static final String BASE_PATH = "cpp/kodlar";
    private static final String REPO_URL = "https://github.com/" + OWNER + "/" + REPO + "/tree/" + BRANCH + "/" + BASE_PATH;
    // Admin/müəllim "Yenilə" düyməsinə tez-tez bassa belə GitHub-u yormasın
    // deyə məcburi minimum məsafə.
    private static final Duration MANUAL_REFRESH_MIN_GAP = Duration.ofSeconds(20);

    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Autentifikasiyasız GitHub API sorğuları saatda cəmi 60-la
    // məhdudlaşır və bu limit Render-in paylaşılan çıxış IP-si üzərindən
    // BÜTÜN müştərilər arasında bölüşülür — asanlıqla tükənib sinxronu
    // uzun müddət 403 ilə dayandıra bilər. Token verilibsə (env: GITHUB_TOKEN,
    // public repo üçün əlavə scope tələb etmir) limit 5000-ə çıxır.
    @Value("${GITHUB_TOKEN:}")
    private String githubToken;

    // volatile — planlaşdırılmış (@Scheduled) thread yeniləyir, HTTP request
    // thread-ləri oxuyur; volatile hər ikisinin ən son dəyəri görməsini təmin edir.
    private volatile GithubCodeTreeResponse cache;
    private volatile Instant lastManualRefresh = Instant.EPOCH;

    // Tətbiq başlayan kimi (initialDelay=0) və sonra hər 5 dəqiqədən bir
    // GitHub-dan yenidən çəkir.
    @Scheduled(fixedRate = 5 * 60 * 1000L, initialDelay = 0L)
    public void refresh() {
        try {
            GithubCodeTreeResponse fetched = fetchFromGithub();
            cache = fetched;
            log.info("GitHub kod arxivi yeniləndi: {} fayl", countFiles(fetched.getRoot()));
        } catch (Exception e) {
            // Uğursuz cəhd köhnə keşi POZMUR — şagird heç olmasa əvvəlki
            // (son uğurlu) versiyanı görməyə davam edir.
            log.warn("GitHub kod arxivini yeniləmək mümkün olmadı, əvvəlki keş saxlanılır: {}", e.getMessage());
        }
    }

    // Cari (keşlənmiş) ağacı qaytarır — heç vaxt birbaşa GitHub-a müraciət
    // etmir, ona görə həmişə sürətlidir. İlk uğurlu sinxrondan əvvəl null olur.
    public GithubCodeTreeResponse getTree() {
        return cache;
    }

    // Planlaşdırılmış 5 dəqiqəni gözləmədən dərhal yeniləmək istəyəndə
    // çağırılır. Minimum məsafə pozulubsa false qaytarır (heç nə etmir).
    public synchronized boolean forceRefresh() {
        Instant now = Instant.now();
        if (Duration.between(lastManualRefresh, now).compareTo(MANUAL_REFRESH_MIN_GAP) < 0) {
            return false;
        }
        lastManualRefresh = now;
        refresh();
        return true;
    }

    private int countFiles(GithubCodeNodeDto node) {
        if (node == null) {
            return 0;
        }
        if ("file".equals(node.getType())) {
            return 1;
        }
        int total = 0;
        for (GithubCodeNodeDto child : node.getChildren()) {
            total += countFiles(child);
        }
        return total;
    }

    // GitHub-un "Git Trees" API-si ilə BÜTÜN repo-nun fayl siyahısını (bir
    // sorğuda, recursive=1) çəkir, cpp/kodlar altındakıları süzür, hər
    // faylın məzmununu raw.githubusercontent.com-dan oxuyur və nəticədə
    // qovluq/fayl ağacını qurur.
    private GithubCodeTreeResponse fetchFromGithub() throws Exception {
        String treeUrl = "https://api.github.com/repos/" + OWNER + "/" + REPO + "/git/trees/" + BRANCH + "?recursive=1";
        HttpRequest.Builder treeRequestBuilder = HttpRequest.newBuilder(URI.create(treeUrl))
            .header("Accept", "application/vnd.github+json")
            .timeout(Duration.ofSeconds(15))
            .GET();
        withAuth(treeRequestBuilder);
        HttpRequest treeRequest = treeRequestBuilder.build();
        HttpResponse<String> treeResponse = httpClient.send(treeRequest, HttpResponse.BodyHandlers.ofString());
        if (treeResponse.statusCode() != 200) {
            throw new IllegalStateException("GitHub tree API " + treeResponse.statusCode() + " qaytardı");
        }
        JsonNode root = objectMapper.readTree(treeResponse.body());
        JsonNode treeArray = root.get("tree");

        // BASE_PATH altındakı bütün "blob" (fayl) girişlərinin NİSBİ yolları.
        List<String> relativeFilePaths = new ArrayList<>();
        for (JsonNode entry : treeArray) {
            String path = entry.get("path").asText();
            String type = entry.get("type").asText();
            if ("blob".equals(type) && path.startsWith(BASE_PATH + "/")) {
                relativeFilePaths.add(path.substring(BASE_PATH.length() + 1));
            }
        }
        Collections.sort(relativeFilePaths);

        MutableNode rootNode = new MutableNode(baseName(BASE_PATH), "", true);
        for (String relativePath : relativeFilePaths) {
            String content = fetchRawContent(relativePath);
            insert(rootNode, relativePath, content);
        }
        return new GithubCodeTreeResponse(REPO_URL, Instant.now(), toDto(rootNode));
    }

    private String fetchRawContent(String relativePath) throws Exception {
        String rawUrl = "https://raw.githubusercontent.com/" + OWNER + "/" + REPO + "/" + BRANCH + "/" + BASE_PATH + "/" + relativePath;
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder(URI.create(rawUrl))
            .timeout(Duration.ofSeconds(15))
            .GET();
        withAuth(requestBuilder);
        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IllegalStateException("Raw content " + response.statusCode() + " (" + relativePath + ")");
        }
        return response.body();
    }

    // Ağacı tədricən (hər faylın yolunu addım-addım) qurmaq üçün dəyişdirilə
    // bilən aralıq node — tam (dəyişməz) DTO-ya yalnız bütün fayllar
    // toplandıqdan SONRA, bir dəfə çevrilir (bax: toDto).
    private static final class MutableNode {
        final String name;
        final String path;
        final boolean isDir;
        String content;
        final Map<String, MutableNode> children = new LinkedHashMap<>();

        MutableNode(String name, String path, boolean isDir) {
            this.name = name;
            this.path = path;
            this.isDir = isDir;
        }
    }

    private void insert(MutableNode root, String relativePath, String content) {
        String[] parts = relativePath.split("/");
        MutableNode current = root;
        StringBuilder pathSoFar = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            boolean isLast = i == parts.length - 1;
            if (pathSoFar.length() > 0) {
                pathSoFar.append('/');
            }
            pathSoFar.append(parts[i]);
            String segmentPath = pathSoFar.toString();
            current = current.children.computeIfAbsent(parts[i], name -> new MutableNode(name, segmentPath, !isLast));
            if (isLast) {
                current.content = content;
            }
        }
    }

    private void withAuth(HttpRequest.Builder builder) {
        if (githubToken != null && !githubToken.isBlank()) {
            builder.header("Authorization", "Bearer " + githubToken);
        }
    }

    private String baseName(String path) {
        int idx = path.lastIndexOf('/');
        return idx == -1 ? path : path.substring(idx + 1);
    }

    // Qovluqlar fayllardan ƏVVƏL, hər qrup daxilində əlifba sırası ilə
    // (alqoritmler faylları 01_, 02_... prefiksli olduğu üçün bu, düzgün
    // rəqəm sırası ilə üst-üstə düşür).
    private GithubCodeNodeDto toDto(MutableNode node) {
        if (!node.isDir) {
            return new GithubCodeNodeDto(node.name, node.path, "file", node.content, null);
        }
        List<MutableNode> sortedChildren = new ArrayList<>(node.children.values());
        sortedChildren.sort(Comparator.<MutableNode, Boolean>comparing(n -> !n.isDir).thenComparing(n -> n.name));
        List<GithubCodeNodeDto> childDtos = new ArrayList<>();
        for (MutableNode child : sortedChildren) {
            childDtos.add(toDto(child));
        }
        return new GithubCodeNodeDto(node.name, node.path, "dir", null, childDtos);
    }
}
