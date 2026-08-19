package az.azcup.backend.controller

import az.azcup.backend.dto.RunRequest
import az.azcup.backend.dto.RunResponse
import az.azcup.backend.judge.JudgeService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Arxada heç bir [az.azcup.backend.entity.Problem] olmadan, ixtiyari C++
 * kodunu compile edib icra edir — "Kod yazma sahəsi" (sərbəst scratchpad)
 * bunu istifadə edir ki, şagird konkret qiymətləndirilən problemə bağlı
 * olmadan da kodunu sınaya bilsin.
 */
@RestController
class RunController(private val judgeService: JudgeService) {

    @PostMapping("/api/run")
    fun run(@Valid @RequestBody request: RunRequest): RunResponse {
        val result = judgeService.runFree(request.sourceCode, request.stdin)
        return RunResponse(result.status, result.stdout, result.stderr, result.executionTimeMs)
    }
}
