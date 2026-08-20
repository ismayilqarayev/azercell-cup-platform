package az.azcup.backend.controller;

import az.azcup.backend.dto.RunRequest;
import az.azcup.backend.dto.RunResponse;
import az.azcup.backend.judge.JudgeResult;
import az.azcup.backend.judge.JudgeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Arxada heç bir {@link az.azcup.backend.entity.Problem} olmadan, ixtiyari C++
 * kodunu compile edib icra edir — "Kod yazma sahəsi" (sərbəst scratchpad)
 * bunu istifadə edir ki, şagird konkret qiymətləndirilən problemə bağlı
 * olmadan da kodunu sınaya bilsin.
 */
@RestController
public class RunController {

    // Kodu compile+icra edən mərkəzi yoxlayıcı servis.
    private final JudgeService judgeService;

    // Spring tərəfindən inject olunan JudgeService-i sahəyə təyin edir.
    public RunController(JudgeService judgeService) {
        this.judgeService = judgeService;
    }

    // Göndərilən kodu (problemə bağlı olmadan) compile edib icra edir və nəticəni qaytarır.
    @PostMapping("/api/run")
    public RunResponse run(@Valid @RequestBody RunRequest request) {
        JudgeResult result = judgeService.runFree(request.getSourceCode(), request.getStdin());
        return new RunResponse(result.getStatus(), result.getStdout(), result.getStderr(), result.getExecutionTimeMs());
    }
}
