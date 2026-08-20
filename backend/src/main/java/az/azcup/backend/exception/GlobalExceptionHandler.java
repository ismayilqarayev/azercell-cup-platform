package az.azcup.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Bütün controller-lər üçün MƏRKƏZLƏŞDİRİLMİŞ xəta emalı. @RestControllerAdvice
// sayəsində heç bir controller-də try/catch yazmağa ehtiyac qalmır — hər hansı
// endpoint-də bu siniflərdən biri atılarsa, Spring avtomatik bura yönləndirir
// və vahid formatda ({status, error, message}) JSON cavab qaytarır.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Öz atdığımız bütün ApiException (və onun alt-sinifləri: NotFoundException,
    // ConflictException və s.) — hər birinin daşıdığı HTTP statusu istifadə olunur.
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        String message;
        if (ex.getMessage() != null) {
            message = ex.getMessage();
        } else {
            message = "";
        }
        return ResponseEntity.status(ex.getStatus())
            .body(errorBody(ex.getStatus(), message));
    }

    // Spring Security-nin login zamanı atdığı xəta — istifadəçiyə "email və ya
    // parol yanlışdır" kimi ümumi mesaj göstərilir (hansının səhv olduğunu
    // dəqiq demirik ki, hücumçuya ipucu verilməsin — təhlükəsizlik təcrübəsi).
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(errorBody(HttpStatus.UNAUTHORIZED, "Yanlış email və ya şifrə"));
    }

    // İstifadəçi giriş edib, amma lazımi rola (məs. ADMIN) sahib deyilsə
    // (SecurityConfig-dəki hasRole() qaydaları) bu xəta tutulur.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorBody(HttpStatus.FORBIDDEN, "İcazə yoxdur"));
    }

    // @Valid ilə işarələnmiş DTO-larda (məs. @NotBlank) doğrulama uğursuz
    // olanda Spring bu istisnanı atır — biz ilk tapılan sahə xətasını
    // oxunaqlı formatda ("sahəAdı: mesaj") istifadəçiyə göstəririk.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        String message;
        if (fieldErrors.isEmpty()) {
            message = "Yanlış sorğu";
        } else {
            message = fieldErrors.get(0).getField() + ": " + fieldErrors.get(0).getDefaultMessage();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody(HttpStatus.BAD_REQUEST, message));
    }

    // Bütün xəta cavabları üçün vahid JSON strukturu yaradır.
    // LinkedHashMap istifadə olunur ki, sahələrin sırası (status, error, message)
    // JSON-da da qorunsun (adi HashMap-da sıra zəmanət edilmir).
    private Map<String, Object> errorBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}
