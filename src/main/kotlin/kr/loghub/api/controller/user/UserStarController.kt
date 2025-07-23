package kr.loghub.api.controller.user

import kr.loghub.api.dto.user.star.UserStarDTO
import kr.loghub.api.service.user.UserStarService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/{userId}/stars")
class UserStarController(private val userStarService: UserStarService) {
    @GetMapping
    fun getStars(
        @PathVariable userId: Long,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<UserStarDTO>> {
        val stars = userStarService.getStars(userId, page)
        return ResponseEntity.ok(stars)
    }
}