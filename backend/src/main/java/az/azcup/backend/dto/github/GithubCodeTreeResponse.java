package az.azcup.backend.dto.github;

import java.time.Instant;

// "GET /api/github-codes" cavabı — kod ağacının özü ilə yanaşı, frontend-in
// "Son sinxron" vaxtını və "GitHub-da aç" linkini göstərə bilməsi üçün
// əlavə metadata daşıyır.
public class GithubCodeTreeResponse {

    private final String repoUrl;
    private final Instant lastSyncedAt;
    private final GithubCodeNodeDto root;

    public GithubCodeTreeResponse(String repoUrl, Instant lastSyncedAt, GithubCodeNodeDto root) {
        this.repoUrl = repoUrl;
        this.lastSyncedAt = lastSyncedAt;
        this.root = root;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public Instant getLastSyncedAt() {
        return lastSyncedAt;
    }

    public GithubCodeNodeDto getRoot() {
        return root;
    }
}
