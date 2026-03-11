package me.loghub.api.service.series

import me.loghub.api.lib.redis.key.series.SeriesTrendingScoreRedisKey
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Service

@Service
class SeriesTrendingScoreService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    private companion object {
        val trendingScoreKey = SeriesTrendingScoreRedisKey()
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    fun updateTrendingScore(seriesId: Long, delta: Double) {
        zSetOps.incrementScore(trendingScoreKey, seriesId.toString(), delta)
    }

    fun clearTrendingScore(seriesId: Long) {
        zSetOps.remove(trendingScoreKey, seriesId.toString())
    }
}