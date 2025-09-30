package me.loghub.api.aspect

import me.loghub.api.constant.redis.RedisKey
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class SeriesTrendingScoreAspect(private val redisTemplate: RedisTemplate<String, String>) {
    private object TrendingScoreDelta {
        const val REVIEW = 1.toDouble()
        const val STAR = 3.toDouble()
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning("execution(* me.loghub.api.service.series.SeriesReviewService.postReview(..)) && args(seriesId, ..))")
    fun updateTrendingScoreAfterPostSeriesReview(seriesId: Long) =
        zSetOps.incrementScore(RedisKey.Series.TRENDING_SCORE, seriesId.toString(), TrendingScoreDelta.REVIEW)

    @AfterReturning("execution(* me.loghub.api.service.series.SeriesReviewService.deleteReview(..)) && args(seriesId, ..))")
    fun updateTrendingScoreAfterDeleteSeriesReview(seriesId: Long) =
        zSetOps.incrementScore(RedisKey.Series.TRENDING_SCORE, seriesId.toString(), -TrendingScoreDelta.REVIEW)

    @AfterReturning("execution(* me.loghub.api.service.series.SeriesStarService.addStar(..)) && args(articleId, ..)")
    fun updateTrendingScoreAfterAddSeriesStar(articleId: Long) =
        zSetOps.incrementScore(RedisKey.Series.TRENDING_SCORE, articleId.toString(), TrendingScoreDelta.STAR)

    @AfterReturning("execution(* me.loghub.api.service.series.SeriesStarService.deleteStar(..)) && args(articleId, ..)")
    fun updateTrendingScoreAfterDeleteSeriesStar(articleId: Long) =
        zSetOps.incrementScore(RedisKey.Series.TRENDING_SCORE, articleId.toString(), -TrendingScoreDelta.STAR)
}