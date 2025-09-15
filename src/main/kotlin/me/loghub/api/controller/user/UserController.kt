package me.loghub.api.controller.user

import me.loghub.api.dto.article.ArticleDTO
import me.loghub.api.dto.article.ArticleSort
import me.loghub.api.dto.question.QuestionDTO
import me.loghub.api.dto.question.QuestionFilter
import me.loghub.api.dto.question.QuestionSort
import me.loghub.api.dto.series.SeriesDTO
import me.loghub.api.dto.series.SeriesSort
import me.loghub.api.dto.user.UserDetailDTO
import me.loghub.api.dto.user.UserProfileDTO
import me.loghub.api.service.user.UserService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {
    @GetMapping("/@{username}")
    fun getUser(@PathVariable username: String): ResponseEntity<UserDetailDTO> {
        val user = userService.getUser(username)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/@{username}/profile")
    fun getUserProfile(@PathVariable username: String): ResponseEntity<UserProfileDTO> {
        val user = userService.getUserProfile(username)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/@{username}/articles")
    fun searchUserArticles(
        @PathVariable username: String,
        @RequestParam(defaultValue = "") query: String,
        @RequestParam(defaultValue = "latest") sort: ArticleSort,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<ArticleDTO>> {
        val articles = userService.searchUserArticles(username, query, sort, page)
        return ResponseEntity.ok(articles)
    }

    @GetMapping("/@{username}/series")
    fun searchUserSeries(
        @PathVariable username: String,
        @RequestParam(defaultValue = "") query: String,
        @RequestParam(defaultValue = "latest") sort: SeriesSort,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<SeriesDTO>> {
        val series = userService.searchUserSeries(username, query, sort, page)
        return ResponseEntity.ok(series)
    }

    @GetMapping("/@{username}/questions")
    fun searchUserQuestions(
        @PathVariable username: String,
        @RequestParam(defaultValue = "") query: String,
        @RequestParam(defaultValue = "latest") sort: QuestionSort,
        @RequestParam(defaultValue = "all") filter: QuestionFilter,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<QuestionDTO>> {
        val questions = userService.searchUserQuestions(username, query, sort, filter, page)
        return ResponseEntity.ok(questions)
    }
}