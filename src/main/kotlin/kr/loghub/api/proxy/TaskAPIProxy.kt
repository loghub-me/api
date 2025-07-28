package kr.loghub.api.proxy

import kr.loghub.api.dto.task.avatar.AvatarGenerateRequest
import kr.loghub.api.dto.task.image.ImageUploadResponse
import kr.loghub.api.dto.task.markdown.MarkdownParseRequest
import kr.loghub.api.dto.task.markdown.MarkdownParseResponse
import kr.loghub.api.dto.task.markdown.MarkdownsParseRequest
import kr.loghub.api.dto.task.markdown.MarkdownsParseResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile

@FeignClient(
    name = "task-api",
    url = "\${task-api.host}",
    configuration = [TaskAPIConfiguration::class],
)
interface TaskAPIProxy {
    @PostMapping("/markdown/parse")
    fun parseMarkdown(@RequestBody request: MarkdownParseRequest): MarkdownParseResponse

    @PostMapping("/markdown/parse")
    fun parseMarkdowns(@RequestBody request: MarkdownsParseRequest): MarkdownsParseResponse

    @PostMapping(
        value = ["/image/upload"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE],
    )
    fun uploadImage(
        @RequestPart("file") file: MultipartFile,
        @RequestPart("userId") userId: Long,
    ): ImageUploadResponse

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
}