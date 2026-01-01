package me.loghub.api.lib.redis.key.article

import me.loghub.api.lib.redis.key.RedisKey

object ArticleTrendingScoreRedisKey : RedisKey {
    operator fun invoke() = "articles:trending_score"
}