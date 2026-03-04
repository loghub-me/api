package me.loghub.api.service.common

import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.dto.task.markdown.MarkdownNormalizeRequest
import me.loghub.api.dto.task.markdown.MarkdownRenderRequest
import me.loghub.api.lib.redis.key.common.MarkdownCacheRedisKey
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.util.sha256
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class MarkdownService(
    private val redisTemplate: RedisTemplate<String, RenderedMarkdownDTO>,
    private val taskAPIProxy: TaskAPIProxy,
) {
    private companion object {
        private const val HASH_ALGORITHM = "SHA-256"
    }

    fun findOrGenerateMarkdownCache(markdown: String): RenderedMarkdownDTO {
        val redisKey = MarkdownCacheRedisKey(sha256(markdown))
        return redisTemplate.opsForValue().get(redisKey) ?: generateMarkdownCache(redisKey, markdown)
    }

    fun findOrGenerateMarkdownCache(markdowns: List<String>): List<RenderedMarkdownDTO> =
        markdowns.map { markdown ->
            val redisKey = MarkdownCacheRedisKey(sha256(markdown))
            redisTemplate.opsForValue().get(redisKey) ?: generateMarkdownCache(redisKey, markdown)
        }

    fun normalizeMarkdown(markdown: String) =
        taskAPIProxy.normalizeMarkdown(MarkdownNormalizeRequest(markdown)).result

    private fun generateMarkdownCache(redisKey: String, markdown: String): RenderedMarkdownDTO {
        val result = taskAPIProxy.renderMarkdown(MarkdownRenderRequest(markdown)).result
        redisTemplate.opsForValue().set(redisKey, result, MarkdownCacheRedisKey.TTL)
        return result
    }
}