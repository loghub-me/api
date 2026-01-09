package me.loghub.api.controller.question

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.DataResponseBody
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.MethodResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.question.QuestionStarService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions/star/{questionId}")
class QuestionStarController(private val questionStarService: QuestionStarService) {
    @GetMapping
    fun existsQuestionStar(
        @PathVariable questionId: Long,
        @AuthenticationPrincipal stargazer: User
    ): ResponseEntity<ResponseBody> {
        val exists = questionStarService.existsStar(questionId, stargazer)
        return DataResponseBody(
            data = exists,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PostMapping
    fun addQuestionStar(
        @PathVariable questionId: Long,
        @AuthenticationPrincipal stargazer: User
    ): ResponseEntity<ResponseBody> {
        val star = questionStarService.addStar(questionId, stargazer)
        return MethodResponseBody(
            id = star.id!!,
            message = ResponseMessage.Star.ADD_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @DeleteMapping
    fun deleteQuestionStar(
        @PathVariable questionId: Long,
        @AuthenticationPrincipal stargazer: User
    ): ResponseEntity<ResponseBody> {
        questionStarService.deleteStar(questionId, stargazer)
        return MessageResponseBody(
            message = ResponseMessage.Star.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}