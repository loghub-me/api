package me.loghub.api.controller.user

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.dto.user.UpdateUsernameDTO
import me.loghub.api.dto.user.UserDetailDTO
import me.loghub.api.dto.user.UserProfileDTO
import me.loghub.api.entity.user.User
import me.loghub.api.service.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

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

    @PutMapping("/username")
    fun updateUsername(
        @RequestBody @Valid requestBody: UpdateUsernameDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ResponseBody> {
        userService.updateUsername(requestBody, user)
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
        userService.updateAvatar(file, user)
        return MessageResponseBody(
            message = ResponseMessage.User.AVATAR_UPDATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PostMapping("/withdraw")
    fun withdraw(): ResponseEntity<ResponseBody> = TODO()
}