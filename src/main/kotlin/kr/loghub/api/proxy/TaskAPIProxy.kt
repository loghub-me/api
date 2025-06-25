package kr.loghub.api.proxy

import kr.loghub.api.dto.internal.avatar.AvatarRenameRequest
import kr.loghub.api.dto.internal.markdown.MarkdownParseRequest
import kr.loghub.api.dto.internal.markdown.MarkdownParseResponse
import kr.loghub.api.dto.internal.markdown.MarkdownsParseRequest
import kr.loghub.api.dto.internal.markdown.MarkdownsParseResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

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

    @PostMapping("/avatar/rename")
    fun renameAvatar(@RequestBody request: AvatarRenameRequest)
}