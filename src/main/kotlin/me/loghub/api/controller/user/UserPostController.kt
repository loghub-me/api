package me.loghub.api.controller.user

import me.loghub.api.dto.article.ArticleDTO
import me.loghub.api.dto.article.ArticleSort
import me.loghub.api.dto.question.QuestionDTO
import me.loghub.api.dto.question.QuestionFilter
import me.loghub.api.dto.question.QuestionSort
import me.loghub.api.dto.series.SeriesDTO
import me.loghub.api.dto.series.SeriesSort
import me.loghub.api.dto.user.post.UserPostProjection
import me.loghub.api.service.user.UserPostService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/@{username}")
class UserPostController(private val userPostService: UserPostService) {
    @GetMapping("/posts")
    fun getUserPosts(@PathVariable username: String): ResponseEntity<List<UserPostProjection>> {
        val posts = userPostService.getUserPosts(username)
        return ResponseEntity.ok(posts)
    }

    @GetMapping("/articles")
    fun searchUserArticles(
        @PathVariable username: String,
        @RequestParam(defaultValue = "") query: String,
        @RequestParam(defaultValue = "latest") sort: ArticleSort,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<ArticleDTO>> {
        val articles = userPostService.searchUserArticles(username, query, sort, page)
        return ResponseEntity.ok(articles)
    }

    @GetMapping("/series")
    fun searchUserSeries(
        @PathVariable username: String,
        @RequestParam(defaultValue = "") query: String,
        @RequestParam(defaultValue = "latest") sort: SeriesSort,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<SeriesDTO>> {
        val series = userPostService.searchUserSeries(username, query, sort, page)
        return ResponseEntity.ok(series)
    }

    @GetMapping("/questions")
    fun searchUserQuestions(
        @PathVariable username: String,
        @RequestParam(defaultValue = "") query: String,
        @RequestParam(defaultValue = "latest") sort: QuestionSort,
        @RequestParam(defaultValue = "all") filter: QuestionFilter,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<QuestionDTO>> {
        val questions = userPostService.searchUserQuestions(username, query, sort, filter, page)
        return ResponseEntity.ok(questions)
    }
}