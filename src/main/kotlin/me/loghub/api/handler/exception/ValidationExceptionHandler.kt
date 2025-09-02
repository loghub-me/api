package me.loghub.api.handler.exception

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.FieldErrorsResponseBody
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.exception.validation.ConflictFieldException
import me.loghub.api.exception.validation.CooldownNotElapsedException
import me.loghub.api.exception.validation.IllegalFieldException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestCookieException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ValidationExceptionHandler {
    @ExceptionHandler(IllegalFieldException::class)
    fun handleException(e: IllegalFieldException): ResponseEntity<ResponseBody> {
        return FieldErrorsResponseBody(
            fieldErrors = mapOf(e.field to e.message),
            status = HttpStatus.BAD_REQUEST
        ).toResponseEntity()
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleException(e: IllegalArgumentException): ResponseEntity<ResponseBody> {
        return MessageResponseBody(
            message = e.message ?: ResponseMessage.Default.INVALID_REQUEST,
            status = HttpStatus.BAD_REQUEST
        ).toResponseEntity()
    }

    @ExceptionHandler(ConflictFieldException::class)
    fun handleException(e: ConflictFieldException): ResponseEntity<ResponseBody> {
        return FieldErrorsResponseBody(
            fieldErrors = mapOf(e.field to e.message),
            status = HttpStatus.CONFLICT
        ).toResponseEntity()
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException(e: MethodArgumentNotValidException): ResponseEntity<ResponseBody> {
        val fieldErrors = e.fieldErrors.associate {
            it.field to (it.defaultMessage ?: ResponseMessage.Default.INVALID_REQUEST)
        }
        return FieldErrorsResponseBody(
            fieldErrors = fieldErrors,
            status = HttpStatus.BAD_REQUEST
        ).toResponseEntity()
    }

    @ExceptionHandler(MissingRequestCookieException::class)
    fun handleException(e: MissingRequestCookieException): ResponseEntity<ResponseBody> {
        return MessageResponseBody(
            message = ResponseMessage.Default.MISSING_COOKIE,
            status = HttpStatus.BAD_REQUEST
        ).toResponseEntity()
    }

    @ExceptionHandler(CooldownNotElapsedException::class)
    fun handleException(e: CooldownNotElapsedException): ResponseEntity<ResponseBody> {
        return MessageResponseBody(
            message = e.message ?: ResponseMessage.Default.COOLDOWN_NOT_ELAPSED,
            status = HttpStatus.TOO_MANY_REQUESTS
        ).toResponseEntity()
    }
}
