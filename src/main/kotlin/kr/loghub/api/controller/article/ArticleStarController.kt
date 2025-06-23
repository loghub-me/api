package kr.loghub.api.controller.article

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.response.DataResponseBody
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.dto.response.MethodResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.article.ArticleStarService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/articles/star/{articleId}")
class ArticleStarController(private val articleStarService: ArticleStarService) {
    @GetMapping
    fun existsArticleStar(
        @PathVariable articleId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ResponseBody> {
        val exists = articleStarService.existsStar(articleId, user)
        return DataResponseBody(
            data = exists,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PostMapping
    fun addArticleStar(
        @PathVariable articleId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ResponseBody> {
        val star = articleStarService.addStar(articleId, user)
        return MethodResponseBody(
            id = star.id!!,
            message = ResponseMessage.Star.ADD_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @DeleteMapping
    fun removeArticleStar(
        @PathVariable articleId: Long,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ResponseBody> {
        articleStarService.removeStar(articleId, user)
        return MessageResponseBody(
            message = ResponseMessage.Star.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}