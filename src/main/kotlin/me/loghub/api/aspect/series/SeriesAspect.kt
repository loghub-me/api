package me.loghub.api.aspect.series

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import me.loghub.api.lib.redis.key.series.SeriesTrendingScoreRedisKey
import me.loghub.api.repository.user.UserActivityRepository
import me.loghub.api.repository.user.saveActivityIgnoreConflict
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class SeriesAspect(
    private val userActivityRepository: UserActivityRepository,
    private val redisTemplate: RedisTemplate<String, String>
) {
    private companion object {
        val logger = KotlinLogging.logger { };
        val trendingScoreKey = SeriesTrendingScoreRedisKey()
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesService.postSeries(..))",
        returning = "postedSeries"
    )
    fun afterPostSeries(postedSeries: Series) {
        addUserActivityAfterPostSeries(postedSeries)
        logAfterPostSeries(postedSeries)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesService.editSeries(..))",
        returning = "editedSeries"
    )
    fun afterEditSeries(editedSeries: Series) {
        logAfterEditSeries(editedSeries)
    }

    @AfterReturning("execution(* me.loghub.api.service.series.SeriesService.deleteSeries(..)) && args(seriesId, writer))")
    fun afterDeleteSeries(seriesId: Long, writer: User) {
        cleanTrendingScoreAfterDeleteSeries(seriesId)
        logAfterDeleteSeries(seriesId, writer)
    }

    private fun addUserActivityAfterPostSeries(postedSeries: Series) {
        userActivityRepository.saveActivityIgnoreConflict(
            UserActivity(
                action = UserActivity.Action.POST_SERIES,
                createdAt = postedSeries.createdAt,
                createdDate = postedSeries.createdAt.toLocalDate(),
                user = postedSeries.writer,
                series = postedSeries
            )
        )
    }

    private fun cleanTrendingScoreAfterDeleteSeries(seriesId: Long) =
        zSetOps.remove(trendingScoreKey, seriesId.toString())

    private fun logAfterPostSeries(series: Series) =
        logger.info { "[Series] posted: { seriesId=${series.id}, writerId=${series.writer.id}, title=\"${series.title}\" }" }

    private fun logAfterEditSeries(series: Series) =
        logger.info { "[Series] edited: { seriesId=${series.id}, writerId=${series.writer.id}, title=\"${series.title}\" }" }

    private fun logAfterDeleteSeries(seriesId: Long, writer: User) =
        logger.info { "[Series] deleted: { seriesId=${seriesId}, writerId=${writer.id} }" }
}