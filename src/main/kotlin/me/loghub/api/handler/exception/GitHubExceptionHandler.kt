package me.loghub.api.handler.exception

import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.exception.github.GitHubUserNotFoundException
import me.loghub.api.exception.github.GitHubVerificationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GitHubExceptionHandler {
    @ExceptionHandler(GitHubUserNotFoundException::class)
    fun handleException(e: GitHubUserNotFoundException): ResponseEntity<ResponseBody> =
        MessageResponseBody(
            message = e.message,
            status = HttpStatus.NOT_FOUND
        ).toResponseEntity()

    @ExceptionHandler(GitHubVerificationException::class)
    fun handleException(e: GitHubVerificationException): ResponseEntity<ResponseBody> =
        MessageResponseBody(
            message = e.message,
            status = HttpStatus.UNPROCESSABLE_ENTITY
        ).toResponseEntity()
}