package az.azcup.backend.exception

import org.springframework.http.HttpStatus

// Bütün öz-yazdığımız (biznes-məntiqli) xətaların əcdadı. HTTP statusunu
// özü ilə daşıyır ki, GlobalExceptionHandler onu tutub düzgün status kodu
// (400, 404, 409 və s.) ilə cavab qaytara bilsin — controller-lərdə try/catch
// yazmağa ehtiyac qalmır, sadəcə "throw ApiException(...)" kifayətdir.
open class ApiException(val status: HttpStatus, message: String) : RuntimeException(message)
