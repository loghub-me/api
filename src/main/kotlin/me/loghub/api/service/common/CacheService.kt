package me.loghub.api.service.common

import me.loghub.api.constant.redis.RedisKey
import me.loghub.api.dto.task.markdown.MarkdownParseRequest
import me.loghub.api.proxy.TaskAPIProxy
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
class CacheService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val taskAPIProxy: TaskAPIProxy,
) {
    private companion object {
        private const val HASH_ALGORITHM = "SHA-256"
    }

    fun findOrGenerateMarkdownCache(markdown: String): String {
        val key = "${RedisKey.MARKDOWN.prefix}:${sha256(markdown)}"
        return redisTemplate.opsForValue().get(key) ?: generateMarkdownCache(key, markdown)
    }

    fun findOrGenerateMarkdownCache(markdowns: List<String>): List<String> =
        markdowns.map { markdown ->
            val key = "${RedisKey.MARKDOWN.prefix}:${sha256(markdown)}"
            redisTemplate.opsForValue().get(key) ?: generateMarkdownCache(key, markdown)
        }

    private fun generateMarkdownCache(key: String, markdown: String): String {
        val html = taskAPIProxy.parseMarkdown(MarkdownParseRequest(markdown)).html
        redisTemplate.opsForValue().set(key, html, RedisKey.MARKDOWN.ttl)
        return html
    }

    private fun sha256(input: String): String = MessageDigest
        .getInstance(HASH_ALGORITHM)
        .digest(input.toByteArray())
        .joinToString("") { "%02x".format(it) }
}