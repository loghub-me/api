package me.loghub.api.controller.auth

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.login.LoginConfirmDTO
import me.loghub.api.dto.auth.login.LoginRequestDTO
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.service.auth.LoginService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/login")
class LoginController(private val loginService: LoginService) {
    @PostMapping("/request")
    fun requestLogin(@RequestBody @Valid requestBody: LoginRequestDTO): ResponseEntity<ResponseBody> {
        loginService.requestLogin(requestBody)
        return MessageResponseBody(
            message = ResponseMessage.Login.REQUEST_SUCCESS,
            status = HttpStatus.OK
        ).toResponseEntity()
    }

    @PostMapping("/confirm")
    fun confirmLogin(@RequestBody @Valid requestBody: LoginConfirmDTO): ResponseEntity<ResponseBody> {
        val (token, session) = loginService.confirmLogin(requestBody)
        val (accessToken, refreshToken) = token
        val responseBody = MessageResponseBody(
            message = ResponseMessage.Login.CONFIRM_SUCCESS,
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(responseBody.status)
            .header(HttpHeaders.AUTHORIZATION, accessToken.authorization)
            .header(HttpHeaders.SET_COOKIE, refreshToken.cookie)
            .header(HttpHeaders.SET_COOKIE, session.cookie)
            .body(responseBody)
    }
}
