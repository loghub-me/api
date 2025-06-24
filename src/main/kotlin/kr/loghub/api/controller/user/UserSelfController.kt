package kr.loghub.api.controller.user

import jakarta.validation.Valid
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.dto.user.UpdateUserPrivacyDTO
import kr.loghub.api.dto.user.UpdateUserProfileDTO
import kr.loghub.api.dto.user.UserPrivacyDTO
import kr.loghub.api.dto.user.UserProfileDTO
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.user.UserSelfService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/self")
class UserSelfController(private val userSelfService: UserSelfService) {
    @GetMapping("/profile")
    fun getProfile(@AuthenticationPrincipal user: User): ResponseEntity<UserProfileDTO> {
        val profile = userSelfService.getProfile(user)
        return ResponseEntity.ok(profile)
    }

    @GetMapping("/privacy")
    fun getPrivacy(@AuthenticationPrincipal user: User): ResponseEntity<UserPrivacyDTO> {
        val privacy = userSelfService.getPrivacy(user)
        return ResponseEntity.ok(privacy)
    }

    @PutMapping("/profile")
    fun updateProfile(
        @RequestBody @Valid requestBody: UpdateUserProfileDTO,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<ResponseBody> {
        userSelfService.updateProfile(requestBody, user)
        return MessageResponseBody(
            message = ResponseMessage.User.PROFILE_UPDATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PutMapping("/privacy")
    fun updatePrivacy(
        @RequestBody @Valid requestBody: UpdateUserPrivacyDTO,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<ResponseBody> {
        userSelfService.updatePrivacy(requestBody, user)
        return MessageResponseBody(
            message = ResponseMessage.User.PRIVACY_UPDATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PostMapping("/withdraw")
    fun withdraw(): ResponseEntity<ResponseBody> = TODO()
}