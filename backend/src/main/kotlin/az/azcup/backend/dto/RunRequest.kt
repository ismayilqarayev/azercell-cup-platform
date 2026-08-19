package az.azcup.backend.dto

import jakarta.validation.constraints.NotBlank

// "POST /api/run" gövdəsi — sərbəst kod yazma sahəsindən gələn kod və
// (istəyə bağlı) stdin. SubmissionRequest-dən fərqli olaraq heç bir
// problem ID-si YOXDUR, çünki bu, konkret bir problemə bağlı deyil.
data class RunRequest(
    @field:NotBlank val sourceCode: String,
    val stdin: String = ""
)
