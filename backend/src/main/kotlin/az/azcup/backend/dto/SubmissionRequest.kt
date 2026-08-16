package az.azcup.backend.dto

import jakarta.validation.constraints.NotBlank

data class SubmissionRequest(
    @field:NotBlank val sourceCode: String
)
