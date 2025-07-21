package kr.loghub.api.controller.user

import kr.loghub.api.dto.response.DataResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.user.UserImageService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/users/image")
class UserImageController(private val userImageService: UserImageService) {
    @PostMapping("/upload")
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