package kr.loghub.api.handler.exception

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.response.FieldErrorsResponseBody
import kr.loghub.api.dto.response.ResponseBody
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException(e: MethodArgumentNotValidException): ResponseEntity<ResponseBody> {
        val fieldErrors = e.fieldErrors.associate {
            it.field to (it.defaultMessage ?: ResponseMessage.INVALID_REQUEST)
        }
        return FieldErrorsResponseBody(
            fieldErrors = fieldErrors,
            status = HttpStatus.BAD_REQUEST
        ).toResponseEntity()
    }
}