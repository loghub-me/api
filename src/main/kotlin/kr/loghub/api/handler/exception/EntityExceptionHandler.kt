package kr.loghub.api.handler.exception

import kr.loghub.api.dto.response.FieldErrorsResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.exception.entity.EntityExistsFieldException
import kr.loghub.api.exception.entity.EntityNotFoundFieldException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class EntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundFieldException::class)
    fun handleException(e: EntityNotFoundFieldException): ResponseEntity<ResponseBody> {
        return FieldErrorsResponseBody(
            fieldErrors = mapOf(e.field to e.message),
            status = HttpStatus.NOT_FOUND
        ).toResponseEntity()
    }

    @ExceptionHandler(EntityExistsFieldException::class)
    fun handleException(e: EntityExistsFieldException): ResponseEntity<ResponseBody> {
        return FieldErrorsResponseBody(
            fieldErrors = mapOf(e.field to e.message),
            status = HttpStatus.CONFLICT
        ).toResponseEntity()
    }
}