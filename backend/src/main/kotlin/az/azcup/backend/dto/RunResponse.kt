package az.azcup.backend.dto

import az.azcup.backend.entity.SubmissionStatus

// "POST /api/run" cavabı. status sahəsi burada ACCEPTED/WRONG_ANSWER
// mənasında YOX, sadəcə "uğurla icra oldu" (ACCEPTED) və ya
// COMPILE_ERROR/RUNTIME_ERROR/TIME_LIMIT_EXCEEDED kimi işlədilir
// (bax: JudgeService.runNoComparison).
data class RunResponse(
    val status: SubmissionStatus,
    val stdout: String,
    val stderr: String,
    val executionTimeMs: Long
)
