package kr.loghub.api.handler.exception

import kr.loghub.api.constant.http.HttpCookie
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.dto.response.ResponseBody
import org.springframework.boot.web.server.Cookie
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {
    @ExceptionHandler(BadCredentialsException::class)
    fun handleException(e: BadCredentialsException): ResponseEntity<ResponseBody> {
        val responseBody = MessageResponseBody(
            message = e.message ?: ResponseMessage.Auth.BAD_CREDENTIALS,
            status = HttpStatus.BAD_REQUEST
        )
        val cookie = generateEmptyCookie(HttpCookie.RefreshToken.NAME)
        return ResponseEntity.status(responseBody.status)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(responseBody)
    }

    private fun generateEmptyCookie(name: String) = ResponseCookie
        .from(name, "")
        .httpOnly(true)
        .secure(true)
        .sameSite(Cookie.SameSite.NONE.name)
        .path("/")
        .maxAge(0)
        .build()
}