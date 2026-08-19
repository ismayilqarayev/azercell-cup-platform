package az.azcup.backend.controller

import az.azcup.backend.dto.RunRequest
import az.azcup.backend.dto.RunResponse
import az.azcup.backend.judge.JudgeService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * Compiles and runs arbitrary C++ code with no backing [az.azcup.backend.entity.Problem] —
 * used by the free-write scratchpad ("Kod yazma sahəsi") so students can try code
 * without it being tied to a specific graded problem.
 */
@RestController
class RunController(private val judgeService: JudgeService) {

    @PostMapping("/api/run")
    fun run(@Valid @RequestBody request: RunRequest): RunResponse {
        val result = judgeService.runFree(request.sourceCode, request.stdin)
        return RunResponse(result.status, result.stdout, result.stderr, result.executionTimeMs)
    }
}
