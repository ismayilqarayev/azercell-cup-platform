package az.azcup.backend.exception

import org.springframework.http.HttpStatus

// 409 Conflict — məs. "bu e-poçtla artıq hesab var" kimi hallarda atılır
// (bax: AuthService.register).
class ConflictException(message: String) : ApiException(HttpStatus.CONFLICT, message)
