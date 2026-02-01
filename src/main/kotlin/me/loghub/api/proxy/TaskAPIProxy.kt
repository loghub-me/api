package me.loghub.api.proxy

import me.loghub.api.dto.task.answer.AnswerGenerateRequest
import me.loghub.api.dto.task.answer.AnswerGenerateResponse
import me.loghub.api.dto.task.avatar.AvatarGenerateRequest
import me.loghub.api.dto.task.image.ImageUploadResponse
import me.loghub.api.dto.task.markdown.*
import me.loghub.api.proxy.config.TaskAPIConfig
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@FeignClient(
    name = "task-api",
    url = "\${task-api.host}",
    configuration = [TaskAPIConfig::class],
)
interface TaskAPIProxy {
    @PostMapping("/markdown/render")
    fun renderMarkdown(@RequestBody request: MarkdownRenderRequest): MarkdownRenderResponse

    @PostMapping("/markdown/render")
    fun renderMarkdowns(@RequestBody request: MarkdownsRenderRequest): MarkdownsRenderResponse

    @PostMapping("/markdown/normalize")
    fun normalizeMarkdown(@RequestBody request: MarkdownNormalizeRequest): MarkdownNormalizeResponse

    @PostMapping(
        value = ["/image/upload"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun uploadImage(
        @RequestPart("file") file: MultipartFile,
        @RequestPart("userId") userId: Long,
    ): ImageUploadResponse

    @DeleteMapping("/image/{userId}")
    fun deleteImages(@PathVariable userId: Long)

    @PostMapping("/avatar/generate")
    fun generateAvatar(@RequestBody request: AvatarGenerateRequest)

    @PostMapping(
        value = ["/avatar/upload"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun uploadAvatar(
        @RequestPart("file") file: MultipartFile,
        @RequestPart("userId") userId: Long,
    )

    @DeleteMapping("/avatar/{userId}")
    fun deleteAvatar(@PathVariable userId: Long)

    @PostMapping("/answer/generate")
    fun generateAnswer(@RequestBody request: AnswerGenerateRequest): AnswerGenerateResponse
}