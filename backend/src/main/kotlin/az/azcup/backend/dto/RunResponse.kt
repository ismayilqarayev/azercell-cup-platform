package az.azcup.backend.dto

import az.azcup.backend.entity.SubmissionStatus

data class RunResponse(
    val status: SubmissionStatus,
    val stdout: String,
    val stderr: String,
    val executionTimeMs: Long
)
