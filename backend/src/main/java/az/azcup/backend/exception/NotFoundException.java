package az.azcup.backend.exception;

import org.springframework.http.HttpStatus;

// 404 Not Found — sorğu edilən problem/mövzu/istifadəçi bazada tapılmayanda
// atılır (məs. ProblemService.getEntity).
public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
