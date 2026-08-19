package az.azcup.backend.exception

import org.springframework.http.HttpStatus

// 404 Not Found — sorğu edilən problem/mövzu/istifadəçi bazada tapılmayanda
// atılır (məs. ProblemService.getProblem).
class NotFoundException(message: String) : ApiException(HttpStatus.NOT_FOUND, message)
