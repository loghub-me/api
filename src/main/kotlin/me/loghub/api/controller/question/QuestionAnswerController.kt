package me.loghub.api.controller.question

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.question.answer.PostQuestionAnswerDTO
import me.loghub.api.dto.question.answer.QuestionAnswerDTO
import me.loghub.api.dto.question.answer.QuestionAnswerForEditDTO
import me.loghub.api.dto.question.answer.RequestGenerateAnswerDTO
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

    @GetMapping("/{answerId}/for-edit")
    fun getAnswerForEdit(
        @PathVariable questionId: Long,
        @PathVariable answerId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<QuestionAnswerForEditDTO> {
        val foundAnswer = questionAnswerService.getAnswerForEdit(questionId, answerId, writer)
        return ResponseEntity.ok(foundAnswer)
    }

    @PostMapping
    fun postAnswer(
        @PathVariable questionId: Long,
        @RequestBody @Valid requestBody: PostQuestionAnswerDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val postedAnswer = questionAnswerService.postAnswer(questionId, requestBody, writer)
        return MethodResponseBody(
            id = postedAnswer.id!!,
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
        val editedAnswer = questionAnswerService.editAnswer(questionId, answerId, requestBody, writer)
        val question = editedAnswer.question
        return RedirectResponseBody(
            pathname = "/questions/${question.writerUsername}/${question.slug}#answer-${editedAnswer.id}",
            message = ResponseMessage.Question.Answer.EDIT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping("/{answerId}")
    fun deleteAnswer(
        @PathVariable questionId: Long,
        @PathVariable answerId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        questionAnswerService.deleteAnswer(questionId, answerId, writer)
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
        @RequestBody @Valid requestBody: RequestGenerateAnswerDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        questionAnswerService.requestGenerateAnswer(questionId, requestBody, writer)

        return MessageResponseBody(
            message = ResponseMessage.Question.Answer.REQUEST_GENERATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}