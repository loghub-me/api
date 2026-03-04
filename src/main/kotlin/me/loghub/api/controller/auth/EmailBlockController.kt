package me.loghub.api.controller.auth

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.service.auth.EmailBlockService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/auth/email/block")
class EmailBlockController(private val emailBlockService: EmailBlockService) {
    @PostMapping
    fun blockEmail(@RequestParam token: UUID): ResponseEntity<ResponseBody> {
        emailBlockService.blockEmail(token)
        return MessageResponseBody(
            message = ResponseMessage.Auth.DENY_EMAIL_SUCCESS,
            status = HttpStatus.OK
        ).toResponseEntity()
    }
}
