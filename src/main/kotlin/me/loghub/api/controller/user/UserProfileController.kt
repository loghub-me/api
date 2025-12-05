package me.loghub.api.controller.user

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.dto.user.UpdateUserProfileDTO
import me.loghub.api.dto.user.UserProfileDTO
import me.loghub.api.entity.user.User
import me.loghub.api.service.user.UserProfileService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/profile")
class UserProfileController(private val userProfileService: UserProfileService) {
    @GetMapping
    fun getProfile(@AuthenticationPrincipal user: User): ResponseEntity<UserProfileDTO> {
        val profile = userProfileService.getProfile(user)
        return ResponseEntity.ok(profile)
    }

    @PutMapping
    fun updateProfile(
        @RequestBody @Valid requestBody: UpdateUserProfileDTO,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<ResponseBody> {
        userProfileService.updateProfile(requestBody, user)
        return MessageResponseBody(
            message = ResponseMessage.User.PROFILE_UPDATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}