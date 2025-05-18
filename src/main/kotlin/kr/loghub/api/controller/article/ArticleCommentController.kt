package kr.loghub.api.controller.article

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.article.comment.ArticleCommentDTO
import kr.loghub.api.dto.article.comment.PostArticleCommentDTO
import kr.loghub.api.dto.response.MethodResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.article.ArticleCommentService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/articles/{articleId}/comments")
class ArticleCommentController(private val articleCommentService: ArticleCommentService) {
    @GetMapping
    fun getComments(
        @PathVariable articleId: Long,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<ArticleCommentDTO>> {
        val comments = articleCommentService.getComments(articleId, page)
        return ResponseEntity.ok(comments)
    }

    @GetMapping("/{commentId}/replies")
    fun getReplies(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
    ): ResponseEntity<List<ArticleCommentDTO>> {
        val comments = articleCommentService.getReplies(articleId, commentId)
        return ResponseEntity.ok(comments)
    }

    @PostMapping
    fun postComment(
        @PathVariable articleId: Long,
        @RequestBody @Valid requestBody: PostArticleCommentDTO,
        @AuthenticationPrincipal writer: User,
    ): ResponseEntity<ResponseBody> {
        val comment = articleCommentService.postComment(articleId, requestBody, writer)
        return MethodResponseBody(
            id = comment.id ?: TODO(),
            message = ResponseMessage.Article.Comment.POST_SUCCESS,
            status = HttpStatus.CREATED
        ).toResponseEntity()
    }

    @DeleteMapping("/{commentId}")
    fun removeComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        articleCommentService.removeComment(articleId, commentId, writer)
        return MethodResponseBody(
            id = commentId,
            message = ResponseMessage.Article.Comment.DELETE_SUCCESS,
            status = HttpStatus.OK
        ).toResponseEntity()
    }
}