package kr.loghub.api.controller.user

import jakarta.validation.Valid
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.response.MessageResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.dto.user.*
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.user.UserSelfService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/users/self")
class UserSelfController(private val userSelfService: UserSelfService) {
    @GetMapping("/recent/posts")
    fun getRecentPosts(@AuthenticationPrincipal user: User): ResponseEntity<List<UserPostDTO>> {
        val posts = userSelfService.getRecentPosts(user)
        return ResponseEntity.ok(posts)
    }

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

    @PutMapping("/username")
    fun updateUsername(
        @RequestBody @Valid requestBody: UpdateUsernameDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ResponseBody> {
        userSelfService.updateUsername(requestBody, user)
        return MessageResponseBody(
            message = ResponseMessage.User.USERNAME_UPDATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PutMapping("/avatar")
    fun updateAvatar(
        @RequestPart("file") file: MultipartFile,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<ResponseBody> {
        userSelfService.updateAvatar(file, user)
        return MessageResponseBody(
            message = ResponseMessage.User.AVATAR_UPDATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
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