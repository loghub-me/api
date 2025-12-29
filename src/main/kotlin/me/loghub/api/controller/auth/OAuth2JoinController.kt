package me.loghub.api.controller.auth

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.join.OAuth2JoinConfirmDTO
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.service.auth.OAuth2JoinService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oauth2/join")
class OAuth2JoinController(private val joinService: OAuth2JoinService) {
    @PostMapping("/confirm")
    fun confirmJoin(@RequestBody @Valid requestBody: OAuth2JoinConfirmDTO): ResponseEntity<ResponseBody> {
        val (token, session) = joinService.confirmJoin(requestBody)
        val (accessToken, refreshToken) = token
        val responseBody = MessageResponseBody(
            message = ResponseMessage.Join.CONFIRM_SUCCESS,
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(responseBody.status)
            .header(HttpHeaders.AUTHORIZATION, accessToken.authorization)
            .header(HttpHeaders.SET_COOKIE, refreshToken.cookie)
            .header(HttpHeaders.SET_COOKIE, session.cookie)
            .body(responseBody)
    }
}
