package az.azcup.backend.dto.github;

import java.util.List;

// GitHub-dan sinxronlaşdırılan bir fayl və ya qovluğu təmsil edən DTO —
// bütün ağacı birbaşa JSON kimi frontend-ə ötürmək üçün istifadə olunur
// (bax: GithubCodeSyncService). type=="file" olduqda content dolu,
// children null olur; type=="dir" olduqda əksinə.
public class GithubCodeNodeDto {

    private final String name;
    private final String path;
    private final String type; // "file" | "dir"
    private final String content;
    private final List<GithubCodeNodeDto> children;

    public GithubCodeNodeDto(String name, String path, String type, String content, List<GithubCodeNodeDto> children) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.content = content;
        this.children = children;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public List<GithubCodeNodeDto> getChildren() {
        return children;
    }
}
