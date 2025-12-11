package me.loghub.api.controller.user

import me.loghub.api.dto.response.DataResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.entity.user.User
import me.loghub.api.lib.ratelimit.RateLimit
import me.loghub.api.service.user.UserImageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.time.temporal.ChronoUnit

@RestController
@RequestMapping("/users/image")
class UserImageController(private val userImageService: UserImageService) {
    @PostMapping("/upload")
    @RateLimit(limit = 10, window = 1, unit = ChronoUnit.HOURS)
    fun uploadImage(
        @RequestPart("file") file: MultipartFile,
        @AuthenticationPrincipal user: User,
    ): ResponseEntity<ResponseBody> {
        val uploadedPath = userImageService.uploadImage(file, user)
        return DataResponseBody(
            data = uploadedPath,
            status = HttpStatus.CREATED
        ).toResponseEntity()
    }
}