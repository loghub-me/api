package me.loghub.api.service.common

import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.dto.task.markdown.MarkdownRenderRequest
import me.loghub.api.lib.redis.key.RedisKey
import me.loghub.api.lib.redis.key.RedisKeys
import me.loghub.api.proxy.TaskAPIProxy
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
class CacheService(
    private val redisTemplate: RedisTemplate<String, RenderedMarkdownDTO>,
    private val taskAPIProxy: TaskAPIProxy,
) {
    private companion object {
        private const val HASH_ALGORITHM = "SHA-256"
    }

    fun findOrGenerateMarkdownCache(markdown: String): RenderedMarkdownDTO {
        val redisKey = RedisKeys.MARKDOWN(sha256(markdown))
        return redisTemplate.opsForValue().get(redisKey.key) ?: generateMarkdownCache(redisKey, markdown)
    }

    fun findOrGenerateMarkdownCache(markdowns: List<String>): List<RenderedMarkdownDTO> =
        markdowns.map { markdown ->
            val redisKey = RedisKeys.MARKDOWN(sha256(markdown))
            redisTemplate.opsForValue().get(redisKey.key) ?: generateMarkdownCache(redisKey, markdown)
        }

    private fun generateMarkdownCache(redisKey: RedisKey, markdown: String): RenderedMarkdownDTO {
        val result = taskAPIProxy.renderMarkdown(MarkdownRenderRequest(markdown)).result
        redisTemplate.opsForValue().set(redisKey.key, result, redisKey.ttl)
        return result
    }

    private fun sha256(input: String): String = MessageDigest
        .getInstance(HASH_ALGORITHM)
        .digest(input.toByteArray())
        .joinToString("") { "%02x".format(it) }
}