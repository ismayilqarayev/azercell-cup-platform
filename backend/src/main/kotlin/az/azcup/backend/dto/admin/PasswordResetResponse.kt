package az.azcup.backend.dto.admin

// Parol sıfırlamanın cavabı — YENİ (açıq mətn) parolu bir dəfəlik göstərir
// ki, admin onu istifadəçiyə çatdıra bilsin. Bazada yalnız bunun hash-i
// saxlanılır, açıq mətn heç yerdə saxlanılmır.
data class PasswordResetResponse(
    val newPassword: String
)
