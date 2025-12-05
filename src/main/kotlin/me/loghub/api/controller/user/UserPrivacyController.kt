package me.loghub.api.controller.user

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MessageResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.dto.user.UpdateUserPrivacyDTO
import me.loghub.api.dto.user.UserPrivacyDTO
import me.loghub.api.entity.user.User
import me.loghub.api.service.user.UserPrivacyService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/privacy")
class UserPrivacyController(private val userPrivacyService: UserPrivacyService) {
    @GetMapping
    fun getPrivacy(@AuthenticationPrincipal user: User): ResponseEntity<UserPrivacyDTO> {
        val privacy = userPrivacyService.getPrivacy(user)
        return ResponseEntity.ok(privacy)
    }

    @PutMapping
    fun updatePrivacy(
        @RequestBody @Valid requestBody: UpdateUserPrivacyDTO,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<ResponseBody> {
        userPrivacyService.updatePrivacy(requestBody, user)
        return MessageResponseBody(
            message = ResponseMessage.User.PRIVACY_UPDATE_SUCCESS,
            status = HttpStatus.OK,
        ).toResponseEntity()
    }
}