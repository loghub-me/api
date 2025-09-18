package me.loghub.api.controller.support

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MethodResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.dto.support.PostSupportInquiryDTO
import me.loghub.api.service.support.SupportInquiryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/support/inquiry")
class SupportInquiryController(private val supportInquiryService: SupportInquiryService) {
    @PostMapping
    fun postInquiry(@RequestBody @Valid requestBody: PostSupportInquiryDTO): ResponseEntity<ResponseBody> {
        val createdInquiry = supportInquiryService.postInquiry(requestBody)
        return MethodResponseBody(
            id = createdInquiry.id!!,
            message = ResponseMessage.Support.Inquiry.POST_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }
}