package me.loghub.api.lib.redis.key.article

import me.loghub.api.lib.redis.key.RedisKey
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

object ArticleDraftRedisKey : RedisKey {
    val TTL = 7.days.toJavaDuration()
    operator fun invoke(articleId: Long) = "articles:$articleId:draft"
}