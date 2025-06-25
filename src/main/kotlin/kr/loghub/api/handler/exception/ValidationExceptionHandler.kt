package kr.loghub.api.handler.exception

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.response.FieldErrorsResponseBody
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.exception.validation.ConflictFieldException
import kr.loghub.api.exception.validation.IllegalFieldException
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
}