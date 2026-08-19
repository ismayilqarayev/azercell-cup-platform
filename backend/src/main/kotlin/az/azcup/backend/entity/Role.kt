package az.azcup.backend.entity

// İstifadəçi rolları — Spring Security-də "ROLE_STUDENT" və s. kimi
// istifadə olunur (bax: UserPrincipal.getAuthorities()). Hüquqlar
// SecurityConfig-də bu rollara görə təyin edilir (məs. /api/admin/**
// yalnız ADMIN roluna açıqdır).
enum class Role {
    STUDENT, TEACHER, ADMIN
}
