package kr.loghub.api.controller.question

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.response.DataResponseBody
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.dto.response.MethodResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.question.QuestionStarService
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
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ResponseBody> {
        val exists = questionStarService.existsStar(questionId, user)
        return DataResponseBody(
            data = exists,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PostMapping
    fun addQuestionStar(
        @PathVariable questionId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ResponseBody> {
        val star = questionStarService.addStar(questionId, user)
        return MethodResponseBody(
            id = star.id!!,
            message = ResponseMessage.Star.ADD_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @DeleteMapping
    fun removeQuestionStar(
        @PathVariable questionId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ResponseBody> {
        questionStarService.removeStar(questionId, user)
        return MessageResponseBody(
            message = ResponseMessage.Star.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}