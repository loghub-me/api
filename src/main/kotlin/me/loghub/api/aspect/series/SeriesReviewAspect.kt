package me.loghub.api.aspect.series

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.dto.notification.NotificationDTO
import me.loghub.api.entity.series.SeriesReview
import me.loghub.api.entity.user.User
import me.loghub.api.lib.redis.key.series.SeriesTrendingScoreRedisKey
import me.loghub.api.service.notification.NotificationService
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class SeriesReviewAspect(
    private val redisTemplate: RedisTemplate<String, String>,
    private val notificationService: NotificationService,
) {
    private object TrendingScoreDelta {
        const val REVIEW = 1.toDouble()
    }

    private companion object {
        val logger = KotlinLogging.logger { };
    }

    private val trendingScoreKey = SeriesTrendingScoreRedisKey()
    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesReviewService.postReview(..))",
        returning = "postedReview"
    )
    fun afterPostReview(postedReview: SeriesReview) {
        val seriesId = postedReview.series.id!!
        updateTrendingScoreAfterPostReview(seriesId)
        sendNotificationsAfterPostReview(postedReview)
        logAfterPostReview(postedReview)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesReviewService.editReview(..))",
        returning = "editedReview"
    )
    fun afterEditReview(editedReview: SeriesReview) {
        logAfterEditReview(editedReview)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesReviewService.deleteReview(..)) && args(seriesId, reviewId, writer)",
    )
    fun afterDeleteReview(seriesId: Long, reviewId: Long, writer: User) {
        updateTrendingScoreAfterDeleteReview(seriesId)
        logAfterDeleteReview(seriesId, reviewId, writer)
    }

    private fun updateTrendingScoreAfterPostReview(seriesId: Long) =
        zSetOps.incrementScore(trendingScoreKey, seriesId.toString(), TrendingScoreDelta.REVIEW)

    private fun updateTrendingScoreAfterDeleteReview(seriesId: Long) =
        zSetOps.incrementScore(trendingScoreKey, seriesId.toString(), -TrendingScoreDelta.REVIEW)

    private fun sendNotificationsAfterPostReview(postedReview: SeriesReview) {
        val series = postedReview.series

        if (series.writer == postedReview.writer) return

        val notification = NotificationDTO(
            href = "/series/${series.writerUsername}/${series.slug}",
            title = series.title,
            message = "@${postedReview.writer.username}님이 회원님의 시리즈에 리뷰를 남겼습니다.",
        )
        notificationService.addNotification(series.writer.id!!, notification)
    }

    private fun logAfterPostReview(review: SeriesReview) =
        logger.info { "[SeriesReview] posted: { seriesId=${review.series.id}, reviewId=${review.id}, writerId=${review.writer.id}, content=\"${review.content}\" }" }

    private fun logAfterEditReview(review: SeriesReview) =
        logger.info { "[SeriesReview] edited: { seriesId=${review.series.id}, reviewId=${review.id}, writerId=${review.writer.id}, content=\"${review.content}\" }" }

    private fun logAfterDeleteReview(seriesId: Long, reviewId: Long, writer: User) =
        logger.info { "[SeriesReview] deleted: { seriesId=${seriesId}, reviewId=${reviewId}, writerId=${writer.id} }" }
}