package az.azcup.backend.exception

import org.springframework.http.HttpStatus

class ConflictException(message: String) : ApiException(HttpStatus.CONFLICT, message)
