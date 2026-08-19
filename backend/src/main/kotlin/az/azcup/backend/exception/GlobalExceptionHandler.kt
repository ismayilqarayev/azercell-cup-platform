package az.azcup.backend.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

// Bütün controller-lər üçün MƏRKƏZLƏŞDİRİLMİŞ xəta emalı. @RestControllerAdvice
// sayəsində heç bir controller-də try/catch yazmağa ehtiyac qalmır — hər hansı
// endpoint-də bu siniflərdən biri atılarsa, Spring avtomatik bura yönləndirir
// və vahid formatda ({status, error, message}) JSON cavab qaytarır.
@RestControllerAdvice
class GlobalExceptionHandler {

    // Öz atdığımız bütün ApiException (və onun alt-sinifləri: NotFoundException,
    // ConflictException və s.) — hər birinin daşıdığı HTTP statusu istifadə olunur.
    @ExceptionHandler(ApiException::class)
    fun handleApiException(ex: ApiException): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(ex.status).body(errorBody(ex.status, ex.message ?: ""))

    // Spring Security-nin login zamanı atdığı xəta — istifadəçiyə "email və ya
    // parol yanlışdır" kimi ümumi mesaj göstərilir (hansının səhv olduğunu
    // dəqiq demirik ki, hücumçuya ipucu verilməsin — təhlükəsizlik təcrübəsi).
    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(ex: BadCredentialsException): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(errorBody(HttpStatus.UNAUTHORIZED, "Yanlış email və ya şifrə"))

    // İstifadəçi giriş edib, amma lazımi rola (məs. ADMIN) sahib deyilsə
    // (SecurityConfig-dəki hasRole() qaydaları) bu xəta tutulur.
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<Map<String, Any>> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorBody(HttpStatus.FORBIDDEN, "İcazə yoxdur"))

    // @Valid ilə işarələnmiş DTO-larda (məs. @NotBlank) doğrulama uğursuz
    // olanda Spring bu istisnanı atır — biz ilk tapılan sahə xətasını
    // oxunaqlı formatda ("sahəAdı: mesaj") istifadəçiyə göstəririk.
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val message = ex.bindingResult.fieldErrors.firstOrNull()
            ?.let { "${it.field}: ${it.defaultMessage}" }
            ?: "Yanlış sorğu"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorBody(HttpStatus.BAD_REQUEST, message))
    }

    // Bütün xəta cavabları üçün vahid JSON strukturu yaradır.
    // linkedMapOf istifadə olunur ki, sahələrin sırası (status, error, message)
    // JSON-da da qorunsun (adi mapOf-da sıra zəmanət edilmir).
    private fun errorBody(status: HttpStatus, message: String): Map<String, Any> = linkedMapOf(
        "status" to status.value(),
        "error" to status.reasonPhrase,
        "message" to message
    )
}
