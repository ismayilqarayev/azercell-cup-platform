package az.azcup.backend.dto

import jakarta.validation.constraints.NotBlank

data class RunRequest(
    @field:NotBlank val sourceCode: String,
    val stdin: String = ""
)
