package me.loghub.api.handler.exception

import feign.FeignException
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class FeignExceptionHandler {
    @ExceptionHandler(FeignException::class)
    fun handleException(e: FeignException): ResponseEntity<ResponseBody> {
        return MessageResponseBody(
            message = ResponseMessage.Default.INTERNAL_SERVER_ERROR,
            status = HttpStatus.INTERNAL_SERVER_ERROR
        ).toResponseEntity()
    }
}