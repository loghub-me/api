package me.loghub.api.aspect

import me.loghub.api.constant.redis.RedisKey
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import me.loghub.api.repository.user.UserActivityRepository
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class SeriesAspect(
    private val userActivityRepository: UserActivityRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private object TrendingScoreDelta {
        const val REVIEW = 1.toDouble()
        const val STAR = 3.toDouble()
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesService.postSeries(..)) && args(.., writer)",
        returning = "series"
    )
    fun afterPostSeries(writer: User, series: Series) {
        val activity = UserActivity(
            action = UserActivity.Action.POST_SERIES,
            user = writer,
            series = series
        )
        userActivityRepository.save(activity)
    }

    @AfterReturning("execution(* me.loghub.api.service.series.SeriesReviewService.postReview(..)) && args(seriesId, ..))")
    fun afterPostReview(seriesId: Long) =
        zSetOps.incrementScore(RedisKey.Series.TRENDING_SCORE, seriesId.toString(), TrendingScoreDelta.REVIEW)

    @AfterReturning("execution(* me.loghub.api.service.series.SeriesReviewService.deleteReview(..)) && args(seriesId, ..))")
    fun afterDeleteReview(seriesId: Long) =
        zSetOps.incrementScore(RedisKey.Series.TRENDING_SCORE, seriesId.toString(), -TrendingScoreDelta.REVIEW)

    @AfterReturning("execution(* me.loghub.api.service.series.SeriesStarService.addStar(..)) && args(seriesId, ..)")
    fun afterAddStar(seriesId: Long) =
        zSetOps.incrementScore(RedisKey.Series.TRENDING_SCORE, seriesId.toString(), TrendingScoreDelta.STAR)

    @AfterReturning("execution(* me.loghub.api.service.series.SeriesStarService.deleteStar(..)) && args(seriesId, ..)")
    fun afterDeleteStar(seriesId: Long) =
        zSetOps.incrementScore(RedisKey.Series.TRENDING_SCORE, seriesId.toString(), -TrendingScoreDelta.STAR)
}