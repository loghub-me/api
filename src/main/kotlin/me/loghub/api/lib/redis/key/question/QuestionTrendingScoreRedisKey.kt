package me.loghub.api.lib.redis.key.question

import me.loghub.api.lib.redis.key.RedisKey

object QuestionTrendingScoreRedisKey : RedisKey {
    operator fun invoke() = "questions:trending_score"
}