package az.azcup.backend.exception

import org.springframework.http.HttpStatus

open class ApiException(val status: HttpStatus, message: String) : RuntimeException(message)
