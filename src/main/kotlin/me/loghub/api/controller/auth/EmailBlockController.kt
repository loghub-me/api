package me.loghub.api.controller.auth

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.auth.email.EmailBlockDTO
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.service.auth.EmailBlockService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/email/block")
class EmailBlockController(private val emailBlockService: EmailBlockService) {
    @PostMapping
    fun blockEmail(@RequestBody @Valid requestBody: EmailBlockDTO): ResponseEntity<ResponseBody> {
        emailBlockService.blockEmail(requestBody)
        return MessageResponseBody(
            message = ResponseMessage.Auth.DENY_EMAIL_SUCCESS,
            status = HttpStatus.OK
        ).toResponseEntity()
    }
}
