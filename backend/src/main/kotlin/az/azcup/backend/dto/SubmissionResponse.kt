package az.azcup.backend.dto

import az.azcup.backend.entity.SubmissionStatus
import java.time.Instant

// Bir kod təqdimatının (submission) API-ya göstərilən forması — həm dərhal
// göndərmə cavabında, həm də tarixçə siyahısında ("GET .../submissions")
// istifadə olunur.
data class SubmissionResponse(
    val id: Long?,
    val problemId: Long?,
    val sourceCode: String,
    val status: SubmissionStatus?,
    val stdout: String?,
    val stderr: String?,
    val executionTimeMs: Long?,
    val submittedAt: Instant?
)
