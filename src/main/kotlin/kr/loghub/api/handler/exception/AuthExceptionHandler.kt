package kr.loghub.api.handler.exception

import com.auth0.jwt.exceptions.JWTVerificationException
import kr.loghub.api.constant.ResponseMessage
import kr.loghub.api.dto.response.MessageResponseBody
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {
    @ExceptionHandler(exception = [BadCredentialsException::class, JWTVerificationException::class])
    fun handleException() = MessageResponseBody(
        status = HttpStatus.UNAUTHORIZED,
        message = ResponseMessage.INVALID_TOKEN
    ).toResponseEntity()
}