package kr.loghub.api.controller.article

import kr.loghub.api.dto.article.ArticleDTO
import kr.loghub.api.dto.article.ArticleDetailDTO
import kr.loghub.api.dto.article.ArticleSort
import kr.loghub.api.service.article.ArticleService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
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
    fun postArticle() {
        TODO()
    }

    @PutMapping("/@{username}/{slug}")
    fun editArticle(@PathVariable username: String, @PathVariable slug: String) {
        TODO()
    }

    @DeleteMapping("/@{username}/{slug}")
    fun deleteArticle(@PathVariable username: String, @PathVariable slug: String) {
        TODO()
    }
}