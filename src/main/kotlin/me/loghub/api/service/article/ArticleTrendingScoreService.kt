package me.loghub.api.service.article

import me.loghub.api.lib.redis.key.article.ArticleTrendingScoreRedisKey
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Service

@Service
class ArticleTrendingScoreService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    private companion object {
        val trendingScoreKey = ArticleTrendingScoreRedisKey()
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    fun updateTrendingScore(articleId: Long, delta: Double) {
        zSetOps.incrementScore(trendingScoreKey, articleId.toString(), delta)
    }

    fun clearTrendingScore(articleId: Long) {
        zSetOps.remove(trendingScoreKey, articleId.toString())
    }
}