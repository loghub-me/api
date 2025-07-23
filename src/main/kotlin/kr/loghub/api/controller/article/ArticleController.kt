package kr.loghub.api.controller.article

import jakarta.validation.Valid
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.article.ArticleDTO
import kr.loghub.api.dto.article.ArticleDetailDTO
import kr.loghub.api.dto.article.ArticleSort
import kr.loghub.api.dto.article.PostArticleDTO
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.dto.response.RedirectResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.article.ArticleService
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

    @PostMapping
    fun postArticle(
        @RequestBody @Valid requestBody: PostArticleDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val createdArticle = articleService.postArticle(requestBody, writer)
        return RedirectResponseBody(
            pathname = "/@${writer.username}/articles/${createdArticle.slug}",
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
            pathname = "/@${writer.username}/articles/${updatedArticle.slug}",
            message = ResponseMessage.Article.EDIT_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @DeleteMapping("/{id}")
    fun removeArticle(
        @PathVariable id: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        articleService.removeArticle(id, writer)
        return MessageResponseBody(
            message = ResponseMessage.Article.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}