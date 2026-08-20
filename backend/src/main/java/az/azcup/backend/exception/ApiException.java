package az.azcup.backend.exception;

import org.springframework.http.HttpStatus;

// Bütün öz-yazdığımız (biznes-məntiqli) xətaların əcdadı. HTTP statusunu
// özü ilə daşıyır ki, GlobalExceptionHandler onu tutub düzgün status kodu
// (400, 404, 409 və s.) ilə cavab qaytara bilsin — controller-lərdə try/catch
// yazmağa ehtiyac qalmır, sadəcə "throw new ApiException(...)" kifayətdir.
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
