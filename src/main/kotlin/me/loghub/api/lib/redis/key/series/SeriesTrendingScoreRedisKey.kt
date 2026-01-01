package me.loghub.api.lib.redis.key.series

import me.loghub.api.lib.redis.key.RedisKey

object SeriesTrendingScoreRedisKey : RedisKey {
    operator fun invoke() = "series:trending_score"
}