package me.loghub.api.aspect.series

import me.loghub.api.constant.redis.RedisKeys
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class SeriesStarAspect(private val redisTemplate: RedisTemplate<String, String>) {
    private object TrendingScoreDelta {
        const val STAR = 3.toDouble()
    }

    private val trendingScoreKey = RedisKeys.Series.TRENDING_SCORE()
    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning("execution(* me.loghub.api.service.series.SeriesStarService.addStar(..)) && args(seriesId, ..)")
    fun afterAddStar(seriesId: Long) {
        updateTrendingScoreAfterAddStar(seriesId)
    }

    @AfterReturning("execution(* me.loghub.api.service.series.SeriesStarService.deleteStar(..)) && args(seriesId, ..)")
    fun afterDeleteStar(seriesId: Long) {
        updateTrendingScoreAfterDeleteStar(seriesId)
    }

    private fun updateTrendingScoreAfterAddStar(seriesId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, seriesId.toString(), TrendingScoreDelta.STAR)

    private fun updateTrendingScoreAfterDeleteStar(seriesId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, seriesId.toString(), -TrendingScoreDelta.STAR)
}