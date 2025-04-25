package kr.loghub.api.handler.exception

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.dto.response.ResponseBody
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {
    @ExceptionHandler(BadCredentialsException::class)
    fun handleException(e: BadCredentialsException): ResponseEntity<ResponseBody> {
        return MessageResponseBody(
            message = e.message ?: ResponseMessage.BAD_CREDENTIALS,
            status = HttpStatus.BAD_REQUEST
        ).toResponseEntity()
    }
}