package me.loghub.api.controller.support

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.dto.support.PostSupportInquiryDTO
import me.loghub.api.dto.support.PostSupportTopicRequestDTO
import me.loghub.api.service.support.SupportService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/support")
class SupportController(private val supportService: SupportService) {
    @PostMapping("/inquiry")
    fun postInquiry(@RequestBody @Valid requestBody: PostSupportInquiryDTO): ResponseEntity<ResponseBody> {
        supportService.postInquiry(requestBody)
        return MessageResponseBody(
            message = ResponseMessage.Support.Inquiry.POST_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @PostMapping("/topic/request")
    fun postRequestTopic(@RequestBody @Valid requestBody: PostSupportTopicRequestDTO): ResponseEntity<ResponseBody> {
        supportService.postTopicRequest(requestBody)
        return MessageResponseBody(
            message = ResponseMessage.Support.TopicRequest.POST_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }
}