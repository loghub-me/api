package kr.loghub.api.service.cache

import kr.loghub.api.constant.redis.RedisKey
import kr.loghub.api.dto.task.markdown.MarkdownParseRequest
import kr.loghub.api.proxy.TaskAPIProxy
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.security.MessageDigest
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

@Service
class CacheService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val taskAPIProxy: TaskAPIProxy,
) {
    companion object {
        private const val HASH_ALGORITHM = "SHA-256"
        private val MARKDOWN_TTL = 7.days.toJavaDuration()
    }

    fun findOrGenerateMarkdownCache(markdown: String): String {
        val markdownKey = "markdown:${sha256(markdown)}"
        return redisTemplate.opsForValue().get(markdownKey) ?: generateMarkdownCache(markdownKey, markdown)
    }

    fun findOrGenerateMarkdownCache(markdowns: List<String>): List<String> =
        markdowns.map { markdown ->
            val markdownKey = "${RedisKey.MARKDOWN}:${sha256(markdown)}"
            redisTemplate.opsForValue().get(markdownKey) ?: generateMarkdownCache(markdownKey, markdown)
        }

    private fun generateMarkdownCache(markdownKey: String, markdown: String): String {
        val html = taskAPIProxy.parseMarkdown(MarkdownParseRequest(markdown)).html
        redisTemplate.opsForValue().set(markdownKey, html, MARKDOWN_TTL)
        return html
    }

    private fun sha256(input: String): String = MessageDigest
        .getInstance(HASH_ALGORITHM)
        .digest(input.toByteArray())
        .joinToString("") { "%02x".format(it) }
}