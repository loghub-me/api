package me.loghub.api.controller.article

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.article.*
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.RedirectResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.service.article.ArticleService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/articles")
class ArticleController(private val articleService: ArticleService) {
    @GetMapping
    fun searchArticles(
        @RequestParam(defaultValue = "") query: String,
        @RequestParam(defaultValue = "latest") sort: ArticleSort,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<ArticleDTO>> {
        val foundArticles = articleService.searchArticles(query, sort, page)
        return ResponseEntity.ok(foundArticles)
    }

    @GetMapping("/@{username}/{slug}")
    fun getArticle(@PathVariable username: String, @PathVariable slug: String): ResponseEntity<ArticleDetailDTO> {
        val foundArticle = articleService.getArticle(username, slug)
        return ResponseEntity.ok(foundArticle)
    }

    @GetMapping("/{id}/for-edit")
    fun getArticleForEdit(
        @PathVariable id: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ArticleForEditDTO> {
        val foundArticle = articleService.getArticleForEdit(id, writer)
        return ResponseEntity.ok(foundArticle)
    }

    @PostMapping
    fun postArticle(
        @RequestBody @Valid requestBody: PostArticleDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val createdArticle = articleService.postArticle(requestBody, writer)
        return RedirectResponseBody(
            pathname = "/articles/${writer.username}/${createdArticle.slug}",
            message = ResponseMessage.Article.POST_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @PutMapping("/{id}")
    fun editArticle(
        @PathVariable id: Long,
        @RequestBody @Valid requestBody: PostArticleDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val updatedArticle = articleService.editArticle(id, requestBody, writer)
        return RedirectResponseBody(
            pathname = "/articles/${writer.username}/${updatedArticle.slug}",
            message = ResponseMessage.Article.EDIT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping("/{id}")
    fun deleteArticle(
        @PathVariable id: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        articleService.deleteArticle(id, writer)
        return MessageResponseBody(
            message = ResponseMessage.Article.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}