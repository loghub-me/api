package me.loghub.api.controller.article

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.article.comment.ArticleCommentDTO
import me.loghub.api.dto.article.comment.PostArticleCommentDTO
import me.loghub.api.dto.response.MethodResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.article.ArticleCommentService
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
        val postedComment = articleCommentService.postComment(articleId, requestBody, writer)
        return MethodResponseBody(
            id = postedComment.id!!,
            message = ResponseMessage.Article.Comment.POST_SUCCESS,
            status = HttpStatus.CREATED
        ).toResponseEntity()
    }

    @PutMapping("/{commentId}")
    fun editComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @RequestBody @Valid requestBody: PostArticleCommentDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val editedComment = articleCommentService.editComment(articleId, commentId, requestBody, writer)
        return MethodResponseBody(
            id = editedComment.id!!,
            message = ResponseMessage.Article.Comment.EDIT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping("/{commentId}")
    fun deleteComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        articleCommentService.deleteComment(articleId, commentId, writer)
        return MethodResponseBody(
            id = commentId,
            message = ResponseMessage.Article.Comment.DELETE_SUCCESS,
            status = HttpStatus.OK
        ).toResponseEntity()
    }
}