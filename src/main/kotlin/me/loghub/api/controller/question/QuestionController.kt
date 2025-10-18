package me.loghub.api.controller.question

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.question.*
import me.loghub.api.dto.response.*
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.question.QuestionService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions")
class QuestionController(private val questionService: QuestionService) {
    @GetMapping
    fun searchQuestions(
        @RequestParam(defaultValue = "") query: String,
        @RequestParam(defaultValue = "latest") sort: QuestionSort,
        @RequestParam(defaultValue = "all") filter: QuestionFilter,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<QuestionDTO>> {
        val foundQuestions = questionService.searchQuestions(query, sort, filter, page)
        return ResponseEntity.ok(foundQuestions)
    }

    @GetMapping("/@{username}/{slug}")
    fun getQuestion(@PathVariable username: String, @PathVariable slug: String): ResponseEntity<QuestionDetailDTO> {
        val foundQuestion = questionService.getQuestion(username, slug)
        return ResponseEntity.ok(foundQuestion)
    }

    @GetMapping("/{id}/answer-generating")
    fun getQuestionAnswerGenerating(@PathVariable id: Long): ResponseEntity<ResponseBody> {
        val data = questionService.getQuestionAnswerGenerating(id)
        return DataResponseBody(data, HttpStatus.OK).toResponseEntity()
    }

    @GetMapping("/{id}/for-edit")
    fun getQuestionForEdit(
        @PathVariable id: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<QuestionForEditDTO> {
        val foundQuestion = questionService.getQuestionForEdit(id, writer)
        return ResponseEntity.ok(foundQuestion)
    }

    @PostMapping
    fun postQuestion(
        @RequestBody @Valid requestBody: PostQuestionDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val createdQuestion = questionService.postQuestion(requestBody, writer)
        return RedirectResponseBody(
            pathname = "/questions/${writer.username}/${createdQuestion.slug}",
            message = ResponseMessage.Question.POST_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @PutMapping("/{id}")
    fun editQuestion(
        @PathVariable id: Long,
        @RequestBody @Valid requestBody: PostQuestionDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val updatedQuestion = questionService.editQuestion(id, requestBody, writer)
        return RedirectResponseBody(
            pathname = "/questions/${writer.username}/${updatedQuestion.slug}",
            message = ResponseMessage.Question.EDIT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping("/{id}")
    fun deleteQuestion(
        @PathVariable id: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        questionService.deleteQuestion(id, writer)
        return MessageResponseBody(
            message = ResponseMessage.Question.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PostMapping("/{id}/close")
    fun closeQuestion(
        @PathVariable id: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val closedQuestion = questionService.closeQuestion(id, writer)
        return MethodResponseBody(
            id = closedQuestion.id!!,
            message = ResponseMessage.Question.CLOSE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}