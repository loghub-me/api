package me.loghub.api.handler.exception

import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.exception.common.TooManyRequestsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RateLimitExceptionHandler {
    @ExceptionHandler(TooManyRequestsException::class)
    fun handleException(e: TooManyRequestsException): ResponseEntity<ResponseBody> =
        MessageResponseBody(
            message = e.message,
            status = HttpStatus.TOO_MANY_REQUESTS
        ).toResponseEntity()
}