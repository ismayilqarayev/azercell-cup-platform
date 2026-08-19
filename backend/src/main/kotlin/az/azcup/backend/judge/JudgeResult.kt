package az.azcup.backend.judge

import az.azcup.backend.entity.SubmissionStatus

// JudgeService-in bir kodu compile+icra etdikdən sonra qaytardığı nəticə.
// Bu obyekt həm SubmissionService (bazaya Submission kimi yazmaq üçün),
// həm də RunController (birbaşa HTTP cavabı kimi qaytarmaq üçün) tərəfindən
// istifadə olunur.
data class JudgeResult(
    val status: SubmissionStatus,
    val stdout: String,
    val stderr: String,
    val executionTimeMs: Long
)
