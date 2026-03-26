package me.loghub.api.controller.user

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.DataResponseBody
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.dto.user.UserDTO
import me.loghub.api.entity.user.User
import me.loghub.api.service.user.UserFollowService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/follows")
class UserFollowController(private val userFollowService: UserFollowService) {
    @GetMapping("/{followeeId}/followers")
    fun getFollowers(
        @PathVariable followeeId: Long,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<UserDTO>> {
        val followers = userFollowService.getFollowers(followeeId, page)
        return ResponseEntity.ok(followers)
    }

    @GetMapping("/{followerId}/followees")
    fun getFollowees(
        @PathVariable followerId: Long,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<UserDTO>> {
        val followees = userFollowService.getFollowees(followerId, page)
        return ResponseEntity.ok(followees)
    }

    @GetMapping("/{followeeId}")
    fun existsFollow(
        @PathVariable followeeId: Long,
        @AuthenticationPrincipal follower: User
    ): ResponseEntity<ResponseBody> {
        val exists = userFollowService.existsFollow(followeeId, follower)
        return DataResponseBody(
            data = exists,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PostMapping("/{followeeId}")
    fun followUser(
        @PathVariable followeeId: Long,
        @AuthenticationPrincipal follower: User
    ): ResponseEntity<ResponseBody> {
        userFollowService.followUser(followeeId, follower)
        return MessageResponseBody(
            message = ResponseMessage.User.Follow.FOLLOW_SUCCESS,
            status = HttpStatus.CREATED,
        ).toResponseEntity()
    }

    @DeleteMapping("/{followeeId}")
    fun unfollowUser(
        @PathVariable followeeId: Long,
        @AuthenticationPrincipal follower: User
    ): ResponseEntity<ResponseBody> {
        userFollowService.unfollowUser(followeeId, follower)
        return MessageResponseBody(
            message = ResponseMessage.User.Follow.UNFOLLOW_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}