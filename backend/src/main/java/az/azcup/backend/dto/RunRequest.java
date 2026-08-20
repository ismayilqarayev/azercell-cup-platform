package az.azcup.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

// "POST /api/run" gövdəsi — sərbəst kod yazma sahəsindən gələn kod və
// (istəyə bağlı) stdin. SubmissionRequest-dən fərqli olaraq heç bir
// problem ID-si YOXDUR, çünki bu, konkret bir problemə bağlı deyil.
public class RunRequest {

    // İcra ediləcək C++ mənbə kodu — boş ola bilməz (bax: @NotBlank).
    @NotBlank
    private final String sourceCode;

    // Proqrama verilən standart giriş (stdin). İstəyə bağlıdır.
    private final String stdin;

    // sourceCode-u birbaşa təyin edir; stdin isə null gələrsə boş sətir ("")
    // ilə əvəz olunur ki, sonrakı kodda null-check-lərə ehtiyac qalmasın.
    public RunRequest(String sourceCode, String stdin) {
        this.sourceCode = sourceCode;
        if (stdin != null) {
            this.stdin = stdin;
        } else {
            this.stdin = "";
        }
    }

    // sourceCode sahəsinin dəyərini qaytarır.
    public String getSourceCode() {
        return sourceCode;
    }

    // stdin sahəsinin dəyərini qaytarır.
    public String getStdin() {
        return stdin;
    }

    // İki RunRequest obyektinin bütün sahələr üzrə məzmunca eyni olub-olmadığını yoxlayır.
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RunRequest that = (RunRequest) o;
        return Objects.equals(sourceCode, that.sourceCode) && Objects.equals(stdin, that.stdin);
    }

    // equals() ilə uyğun hash kodu yaradır (Object müqaviləsinə görə equals()
    // true olan obyektlərin hashCode()-u da eyni olmalıdır) — Objects.hash(...)
    // bütün sahələrin hash-lərini birləşdirir.
    @Override
    public int hashCode() {
        return Objects.hash(sourceCode, stdin);
    }

    // Debug/log məqsədləri üçün obyektin bütün sahələrini ehtiva edən mətn təsvirini yaradır.
    @Override
    public String toString() {
        return "RunRequest{" +
            "sourceCode='" + sourceCode + '\'' +
            ", stdin='" + stdin + '\'' +
            '}';
    }
}
