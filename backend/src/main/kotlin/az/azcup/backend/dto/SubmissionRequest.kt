package az.azcup.backend.dto

import jakarta.validation.constraints.NotBlank

// "POST /api/problems/{id}/submissions" gövdəsi — şagirdin göndərdiyi
// C++ kodu. @NotBlank sayəsində boş/yalnız-boşluqlu kod Spring tərəfindən
// controller-ə çatmazdan əvvəl avtomatik rədd edilir (bax: GlobalExceptionHandler.handleValidation).
data class SubmissionRequest(
    @field:NotBlank val sourceCode: String
)
