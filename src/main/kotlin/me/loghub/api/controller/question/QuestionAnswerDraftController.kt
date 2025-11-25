package me.loghub.api.controller.question

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.common.UpdateDraftDTO
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.question.QuestionAnswerDraftService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions/{questionId}/answers/{answerId}/draft")
class QuestionAnswerDraftController(private val questionAnswerDraftService: QuestionAnswerDraftService) {
    @PatchMapping
    fun updateAnswerDraft(
        @PathVariable questionId: Long,
        @PathVariable answerId: Long,
        @RequestBody @Valid requestBody: UpdateDraftDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        questionAnswerDraftService.updateAnswerDraft(questionId, answerId, requestBody, writer)
        return MessageResponseBody(
            message = ResponseMessage.Question.Answer.Draft.UPDATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping
    fun deleteAnswerDraft(
        @PathVariable questionId: Long,
        @PathVariable answerId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        questionAnswerDraftService.deleteAnswerDraft(questionId, answerId, writer)
        return MessageResponseBody(
            message = ResponseMessage.Question.Answer.Draft.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}