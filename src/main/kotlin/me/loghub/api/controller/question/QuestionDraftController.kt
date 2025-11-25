package me.loghub.api.controller.question

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.common.UpdateDraftDTO
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.question.QuestionDraftService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions/{id}/draft")
class QuestionDraftController(private val questionDraftService: QuestionDraftService) {
    @PatchMapping
    fun updateQuestionDraft(
        @PathVariable id: Long,
        @RequestBody @Valid requestBody: UpdateDraftDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        questionDraftService.updateQuestionDraft(id, requestBody, writer)
        return MessageResponseBody(
            message = ResponseMessage.Question.Draft.UPDATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping
    fun deleteQuestionDraft(
        @PathVariable id: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        questionDraftService.deleteQuestionDraft(id, writer)
        return MessageResponseBody(
            message = ResponseMessage.Question.Draft.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}