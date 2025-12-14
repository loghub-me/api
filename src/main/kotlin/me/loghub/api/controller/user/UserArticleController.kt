package me.loghub.api.controller.user

import me.loghub.api.dto.article.ArticleForImportDTO
import me.loghub.api.dto.article.ArticleUnpublishedDTO
import me.loghub.api.entity.user.User
import me.loghub.api.service.user.UserArticleService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/articles")
class UserArticleController(private val userArticleService: UserArticleService) {
    @GetMapping("/unpublished")
    fun searchUnpublishedArticles(
        @RequestParam(defaultValue = "") query: String,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<List<ArticleUnpublishedDTO>> {
        val articles = userArticleService.searchUnpublishedArticles(query, user)
        return ResponseEntity.ok(articles)
    }

    @GetMapping("/for-import")
    fun searchArticlesForImport(
        @RequestParam(defaultValue = "") query: String,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<List<ArticleForImportDTO>> {
        val articles = userArticleService.searchArticlesForImport(query, user)
        return ResponseEntity.ok(articles)
    }
}