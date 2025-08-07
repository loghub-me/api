package me.loghub.api.controller.auth

import me.loghub.api.constant.http.HttpCookie
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.service.auth.RefreshService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/auth/refresh")
class RefreshController(private val refreshService: RefreshService) {
    @PostMapping
    fun refreshToken(
        @CookieValue(
            HttpCookie.RefreshToken.NAME,
            required = false
        ) refreshToken: UUID?
    ): ResponseEntity<ResponseBody> {
        val token = refreshService.refreshToken(refreshToken)
        val responseBody = MessageResponseBody(
            message = ResponseMessage.Token.REFRESH_SUCCESS,
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(responseBody.status)
            .header(HttpHeaders.AUTHORIZATION, token.authorization)
            .header(HttpHeaders.SET_COOKIE, token.cookie)
            .body(responseBody)
    }
}
