package kr.loghub.api.controller

import kr.loghub.api.constant.ResponseMessage
import kr.loghub.api.dto.response.DataResponseBody
import kr.loghub.api.dto.response.MessageResponseBody
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health")
class HealthController {
    @GetMapping
    fun checkHealth() = ResponseEntity.ok(ResponseMessage.IM_GOOD)

    @GetMapping("/message")
    fun getMessageBody() = ResponseEntity.ok(
        MessageResponseBody(
            message = ResponseMessage.IM_GOOD,
            status = HttpStatus.OK
        )
    )

    @GetMapping("/data")
    fun getDataBody() = ResponseEntity.ok(
        DataResponseBody<MessageResponseBody>(
            data = MessageResponseBody(
                message = ResponseMessage.IM_GOOD,
                status = HttpStatus.CREATED
            ),
            status = HttpStatus.OK
        )
    )
}