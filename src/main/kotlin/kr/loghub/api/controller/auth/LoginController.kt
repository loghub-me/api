package kr.loghub.api.controller.auth

import jakarta.validation.Valid
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.auth.LoginConfirmDTO
import kr.loghub.api.dto.auth.LoginRequestDTO
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.service.auth.LoginService
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
        val token = loginService.confirmLogin(requestBody)
        val responseBody = MessageResponseBody(
            message = ResponseMessage.Login.CONFIRM_SUCCESS,
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(responseBody.status)
            .header(HttpHeaders.AUTHORIZATION, token.authorization)
            .header(HttpHeaders.SET_COOKIE, token.cookie)
            .body(responseBody)
    }
}
