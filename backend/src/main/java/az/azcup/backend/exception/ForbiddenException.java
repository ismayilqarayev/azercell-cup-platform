package az.azcup.backend.exception;

import org.springframework.http.HttpStatus;

// 403 Forbidden — istifadəçi giriş edib, amma bu KONKRET resursa (məs.
// başqa müəllimin qrupuna) sahib olmadığı üçün əməliyyata icazə verilmir.
// SecurityConfig-dəki rol-əsaslı 403-dən (AccessDeniedException) fərqli
// olaraq, bu, "eyni rol, amma özgə məlumat" hallarını əhatə edir
// (bax: GroupService.requireOwnership).
public class ForbiddenException extends ApiException {
    // Mesajı 403 FORBIDDEN statusu ilə birlikdə ApiException-a ötürür.
    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
