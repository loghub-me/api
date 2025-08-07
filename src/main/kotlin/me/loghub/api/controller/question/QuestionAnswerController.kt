package me.loghub.api.controller.question

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.question.answer.PostQuestionAnswerDTO
import me.loghub.api.dto.question.answer.QuestionAnswerDTO
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.MethodResponseBody
import me.loghub.api.dto.response.RedirectResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.question.QuestionAnswerService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions/{questionId}/answers")
class QuestionAnswerController(private val questionAnswerService: QuestionAnswerService) {
    @GetMapping
    fun getAnswers(@PathVariable questionId: Long): ResponseEntity<List<QuestionAnswerDTO>> {
        val answers = questionAnswerService.getAnswers(questionId)
        return ResponseEntity.ok(answers)
    }

    @PostMapping
    fun postAnswer(
        @PathVariable questionId: Long,
        @RequestBody @Valid requestBody: PostQuestionAnswerDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val createdAnswer = questionAnswerService.postAnswer(questionId, requestBody, writer)
        val question = createdAnswer.question
        return RedirectResponseBody(
            pathname = "/@${question.writerUsername}/questions/${question.slug}",
            message = ResponseMessage.Question.Answer.POST_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @PutMapping("/{answerId}")
    fun editAnswer(
        @PathVariable questionId: Long,
        @PathVariable answerId: Long,
        @RequestBody @Valid requestBody: PostQuestionAnswerDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val updatedAnswer = questionAnswerService.editAnswer(questionId, answerId, requestBody, writer)
        val question = updatedAnswer.question
        return RedirectResponseBody(
            pathname = "/@${question.writerUsername}/questions/${question.slug}",
            message = ResponseMessage.Question.Answer.EDIT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping("/{answerId}")
    fun removeAnswer(
        @PathVariable questionId: Long,
        @PathVariable answerId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        questionAnswerService.removeAnswer(questionId, answerId, writer)
        return MethodResponseBody(
            id = answerId,
            message = ResponseMessage.Question.Answer.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PostMapping("/{answerId}/accept")
    fun acceptAnswer(
        @PathVariable questionId: Long,
        @PathVariable answerId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val acceptedAnswer = questionAnswerService.acceptAnswer(questionId, answerId, writer)
        return MethodResponseBody(
            id = acceptedAnswer.id!!,
            message = ResponseMessage.Question.Answer.ACCEPT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PostMapping("/generate")
    fun requestGenerateAnswer(
        @PathVariable questionId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        questionAnswerService.requestGenerateAnswer(questionId, writer)

        return MessageResponseBody(
            message = ResponseMessage.Question.Answer.REQUEST_GENERATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}