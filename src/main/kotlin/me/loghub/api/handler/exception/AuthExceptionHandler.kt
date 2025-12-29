package me.loghub.api.handler.exception

import me.loghub.api.config.ClientConfig
import me.loghub.api.dto.auth.SessionDTO
import me.loghub.api.dto.auth.token.RefreshToken
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.exception.auth.BadOTPException
import me.loghub.api.exception.auth.BadRefreshTokenException
import me.loghub.api.exception.auth.PermissionDeniedException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {
    @ExceptionHandler(BadOTPException::class)
    fun handleException(e: BadOTPException): ResponseEntity<ResponseBody> =
        MessageResponseBody(
            message = e.message,
            status = HttpStatus.BAD_REQUEST
        ).toResponseEntity()

    @ExceptionHandler(BadRefreshTokenException::class)
    fun handleException(e: BadRefreshTokenException): ResponseEntity<ResponseBody> {
        val responseBody = MessageResponseBody(
            message = e.message,
            status = HttpStatus.BAD_REQUEST
        )
        val emptyRefreshTokenCookie = generateEmptyRefreshTokenCookie()
        val emptySessionCookie = generateEmptySessionCookie()
        return ResponseEntity.status(responseBody.status)
            .header(HttpHeaders.SET_COOKIE, emptyRefreshTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, emptySessionCookie.toString())
            .body(responseBody)
    }

    @ExceptionHandler(PermissionDeniedException::class)
    fun handleException(e: PermissionDeniedException): ResponseEntity<ResponseBody> =
        MessageResponseBody(
            message = e.message,
            status = HttpStatus.FORBIDDEN
        ).toResponseEntity()

    private fun generateEmptyRefreshTokenCookie() = ResponseCookie
        .from(RefreshToken.Cookie.NAME, "")
        .domain(ClientConfig.DOMAIN)
        .httpOnly(true)
        .secure(true)
        .path(RefreshToken.Cookie.PATH)
        .sameSite(RefreshToken.Cookie.SAME_SITE)
        .maxAge(0)
        .build()

    private fun generateEmptySessionCookie() = ResponseCookie
        .from(SessionDTO.Cookie.NAME, "")
        .domain(ClientConfig.DOMAIN)
        .httpOnly(true)
        .secure(true)
        .path(SessionDTO.Cookie.PATH)
        .sameSite(SessionDTO.Cookie.SAME_SITE)
        .maxAge(0)
        .build()
}