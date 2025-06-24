package kr.loghub.api.controller.user

import kr.loghub.api.dto.article.ArticleDTO
import kr.loghub.api.dto.article.ArticleSort
import kr.loghub.api.dto.question.QuestionDTO
import kr.loghub.api.dto.question.QuestionFilter
import kr.loghub.api.dto.question.QuestionSort
import kr.loghub.api.dto.user.UserDetailDTO
import kr.loghub.api.service.user.UserService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {
    @GetMapping("/@{username}")
    fun getProfile(@PathVariable username: String): ResponseEntity<UserDetailDTO> {
        val user = userService.getUser(username)
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