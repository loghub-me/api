package me.loghub.api.service.question

import me.loghub.api.lib.redis.key.question.QuestionTrendingScoreRedisKey
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Service

@Service
class QuestionTrendingScoreService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    private companion object {
        val trendingScoreKey = QuestionTrendingScoreRedisKey()
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    fun updateTrendingScore(questionId: Long, delta: Double) {
        zSetOps.incrementScore(trendingScoreKey, questionId.toString(), delta)
    }

    fun clearTrendingScore(questionId: Long) {
        zSetOps.remove(trendingScoreKey, questionId.toString())
    }
}