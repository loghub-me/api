package me.loghub.api.handler.exception

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.FieldErrorsResponseBody
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.exception.entity.EntityExistsException
import me.loghub.api.exception.entity.EntityExistsFieldException
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.exception.entity.EntityNotFoundFieldException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class EntityExceptionHandler {
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleException(e: EntityNotFoundException): ResponseEntity<ResponseBody> {
        return MessageResponseBody(
            message = e.message ?: ResponseMessage.Default.NOT_FOUND,
            status = HttpStatus.NOT_FOUND
        ).toResponseEntity()
    }

    @ExceptionHandler(EntityNotFoundFieldException::class)
    fun handleException(e: EntityNotFoundFieldException): ResponseEntity<ResponseBody> {
        return FieldErrorsResponseBody(
            fieldErrors = mapOf(e.field to e.message),
            status = HttpStatus.NOT_FOUND
        ).toResponseEntity()
    }

    @ExceptionHandler(EntityExistsException::class)
    fun handleException(e: EntityExistsException): ResponseEntity<ResponseBody> {
        return MessageResponseBody(
            message = e.message ?: ResponseMessage.Default.ALREADY_EXISTS,
            status = HttpStatus.CONFLICT
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