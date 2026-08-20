package az.azcup.backend.exception;

import org.springframework.http.HttpStatus;

// 409 Conflict — məs. "bu e-poçtla artıq hesab var" kimi hallarda atılır
// (bax: AuthService.register).
public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
