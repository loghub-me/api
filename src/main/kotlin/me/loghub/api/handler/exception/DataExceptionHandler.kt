package me.loghub.api.handler.exception

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DataExceptionHandler {
    @ExceptionHandler(EmptyResultDataAccessException::class)
    fun handleException(e: EmptyResultDataAccessException): ResponseEntity<ResponseBody> =
        MessageResponseBody(
            message = ResponseMessage.Default.NOT_FOUND,
            status = HttpStatus.NOT_FOUND,
        ).toResponseEntity()

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleException(e: DataIntegrityViolationException): ResponseEntity<ResponseBody> {
        e.printStackTrace()
        return MessageResponseBody(
            message = ResponseMessage.Default.CONFLICT,
            status = HttpStatus.CONFLICT,
        ).toResponseEntity()
    }
}