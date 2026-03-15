package me.loghub.api.service.question

import me.loghub.api.lib.redis.key.question.QuestionTrendingScoreRedisKey
import me.loghub.api.service.common.TrendingScoreService
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Service

@Service
class QuestionTrendingScoreService(
    private val redisTemplate: RedisTemplate<String, String>
) : TrendingScoreService {
    private companion object {
        val trendingScoreKey = QuestionTrendingScoreRedisKey()
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    override fun updateTrendingScore(id: Long, delta: Double) {
        zSetOps.incrementScore(trendingScoreKey, id.toString(), delta)
    }

    override fun clearTrendingScore(id: Long) {
        zSetOps.remove(trendingScoreKey, id.toString())
    }
}