package me.loghub.api.controller.user

import me.loghub.api.dto.user.activity.UserActivityProjection
import me.loghub.api.dto.user.activity.UserActivitySummaryDTO
import me.loghub.api.service.user.UserActivityService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/users/{userId}/activities")
class UserActivityController(private val userActivityService: UserActivityService) {
    @GetMapping
    fun getActivities(
        @PathVariable userId: Long,
        @RequestParam(required = false) from: LocalDate = LocalDate.now().minusYears(1),
        @RequestParam(required = false) to: LocalDate = LocalDate.now(),
    ): ResponseEntity<List<UserActivitySummaryDTO>> {
        val summaries = userActivityService.getActivitySummaries(userId, from, to)
        return ResponseEntity.ok(summaries)
    }

    @GetMapping("/{date}")
    fun getActivity(
        @PathVariable userId: Long,
        @PathVariable date: LocalDate
    ): ResponseEntity<List<UserActivityProjection>> {
        val activities = userActivityService.getActivities(userId, date)
        return ResponseEntity.ok(activities)
    }
}