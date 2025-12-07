package me.loghub.api.controller.user

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.dto.user.UpdateUserGitHubDTO
import me.loghub.api.dto.user.UserGitHubDTO
import me.loghub.api.entity.user.User
import me.loghub.api.service.user.UserGitHubService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/github")
class UserGitHubController(private val userGitHubService: UserGitHubService) {
    @GetMapping
    fun getGitHub(@AuthenticationPrincipal user: User): ResponseEntity<UserGitHubDTO> {
        val github = userGitHubService.getGitHub(user)
        return ResponseEntity.ok(github)
    }

    @PutMapping
    fun updateGitHub(
        @RequestBody @Valid requestBody: UpdateUserGitHubDTO,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ResponseBody> {
        userGitHubService.updateGitHub(requestBody, user)
        return MessageResponseBody(
            message = ResponseMessage.User.GitHub.UPDATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
    
    @DeleteMapping
    fun deleteGitHub(@AuthenticationPrincipal user: User): ResponseEntity<ResponseBody> {
        userGitHubService.deleteGitHub(user)
        return MessageResponseBody(
            message = ResponseMessage.User.GitHub.DELETE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }

    @PostMapping("/verify")
    fun verifyGitHub(@AuthenticationPrincipal user: User): ResponseEntity<ResponseBody> {
        userGitHubService.verifyGitHub(user)
        return MessageResponseBody(
            message = ResponseMessage.User.GitHub.VERIFICATION_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}