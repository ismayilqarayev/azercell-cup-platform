package az.azcup.backend.judge

import az.azcup.backend.entity.SubmissionStatus

data class JudgeResult(
    val status: SubmissionStatus,
    val stdout: String,
    val stderr: String,
    val executionTimeMs: Long
)
