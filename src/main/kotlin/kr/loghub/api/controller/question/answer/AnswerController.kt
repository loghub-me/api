package kr.loghub.api.controller.question.answer

import jakarta.validation.Valid
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.question.answer.PostAnswerDTO
import kr.loghub.api.dto.response.MethodResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.question.answer.AnswerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions/{questionId}/answers")
class AnswerController(private val answerService: AnswerService) {
    @PostMapping
    fun postAnswer(
        @PathVariable questionId: Long,
        @RequestBody @Valid requestBody: PostAnswerDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val createdAnswer = answerService.postAnswer(questionId, requestBody, writer)
        return MethodResponseBody(
            id = createdAnswer.id ?: TODO(),
            message = ResponseMessage.Answer.POST_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @PostMapping("/{answerId}/accept")
    fun acceptAnswer(
        @PathVariable questionId: Long,
        @PathVariable answerId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val acceptedAnswer = answerService.acceptAnswer(questionId, answerId, writer)
        return MethodResponseBody(
            id = acceptedAnswer.id ?: TODO(),
            message = ResponseMessage.Answer.ACCEPT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PutMapping("/{answerId}")
    fun editAnswer(
        @PathVariable questionId: Long,
        @PathVariable answerId: Long,
        @RequestBody @Valid requestBody: PostAnswerDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val updatedAnswer = answerService.editAnswer(questionId, answerId, requestBody, writer)
        return MethodResponseBody(
            id = updatedAnswer.id ?: TODO(),
            message = ResponseMessage.Answer.EDIT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping("/{answerId}")
    fun removeAnswer(
        @PathVariable questionId: Long,
        @PathVariable answerId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        answerService.removeAnswer(questionId, answerId, writer)
        return MethodResponseBody(
            id = answerId,
            message = ResponseMessage.Answer.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}