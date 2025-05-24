package kr.loghub.api.proxy

import kr.loghub.api.dto.task.markdown.MarkdownParseRequest
import kr.loghub.api.dto.task.markdown.MarkdownParseResponse
import kr.loghub.api.dto.task.markdown.MarkdownsParseRequest
import kr.loghub.api.dto.task.markdown.MarkdownsParseResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "task-api",
    url = "\${task-api.host}/task-api",
    configuration = [TaskAPIConfiguration::class],
)
interface TaskAPIProxy {
    @PostMapping("/markdown/parse")
    fun parseMarkdown(@RequestBody request: MarkdownParseRequest): MarkdownParseResponse

    @PostMapping("/markdown/parse")
    fun parseMarkdowns(@RequestBody request: MarkdownsParseRequest): MarkdownsParseResponse
}