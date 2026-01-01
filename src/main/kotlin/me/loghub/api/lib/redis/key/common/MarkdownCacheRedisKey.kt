package me.loghub.api.lib.redis.key.common

import me.loghub.api.lib.redis.key.RedisKey
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

object MarkdownCacheRedisKey : RedisKey {
    val TTL = 1.days.toJavaDuration()
    operator fun invoke(hashedMarkdown: String) = "markdown:$hashedMarkdown"
}