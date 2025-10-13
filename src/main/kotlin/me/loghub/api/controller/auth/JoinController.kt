package me.loghub.api.controller.auth

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.join.JoinConfirmDTO
import me.loghub.api.dto.auth.join.JoinRequestDTO
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.service.auth.JoinService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/join")
class JoinController(private val joinService: JoinService) {
    @PostMapping("/request")
    fun requestJoin(@RequestBody @Valid requestBody: JoinRequestDTO): ResponseEntity<ResponseBody> {
        joinService.requestJoin(requestBody)
        return MessageResponseBody(
            message = ResponseMessage.Join.REQUEST_SUCCESS,
            status = HttpStatus.OK
        ).toResponseEntity()
    }

    @PostMapping("/confirm")
    fun confirmJoin(@RequestBody @Valid requestBody: JoinConfirmDTO): ResponseEntity<ResponseBody> {
        val token = joinService.confirmJoin(requestBody)
        val responseBody = MessageResponseBody(
            message = ResponseMessage.Join.CONFIRM_SUCCESS,
            status = HttpStatus.OK,
        )
        return ResponseEntity.status(responseBody.status)
            .header(HttpHeaders.AUTHORIZATION, token.authorization)
            .header(HttpHeaders.SET_COOKIE, token.cookie)
            .body(responseBody)
    }
}
