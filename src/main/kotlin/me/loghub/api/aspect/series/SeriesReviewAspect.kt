package me.loghub.api.aspect.series

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.constant.redis.RedisKeys
import me.loghub.api.dto.notification.NotificationDTO
import me.loghub.api.entity.series.SeriesReview
import me.loghub.api.entity.user.User
import me.loghub.api.service.user.NotificationService
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId

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

    private val trendingScoreKey = RedisKeys.Series.TRENDING_SCORE()
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
        zSetOps.incrementScore(trendingScoreKey.key, seriesId.toString(), TrendingScoreDelta.REVIEW)

    private fun updateTrendingScoreAfterDeleteReview(seriesId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, seriesId.toString(), -TrendingScoreDelta.REVIEW)

    private fun sendNotificationsAfterPostReview(postedReview: SeriesReview) {
        val series = postedReview.series

        val href = "/series/${series.writerUsername}/${series.slug}"
        val timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (series.writer == postedReview.writer) return
        val message = "@${postedReview.writer.username}님이 회원님의 아티클에 댓글을 남겼습니다."
        val notification = NotificationDTO(href, message, timestamp)
        notificationService.addNotification(series.writer.id!!, notification)
    }

    private fun logAfterPostReview(postedReview: SeriesReview) =
        logger.info { "[SeriesReview] posted: { seriesId=${postedReview.series.id}, reviewId=${postedReview.id}, writerId=${postedReview.writer.id}, content=\"${postedReview.content}\" }" }

    private fun logAfterEditReview(editedReview: SeriesReview) =
        logger.info { "[SeriesReview] edited: { seriesId=${editedReview.series.id}, reviewId=${editedReview.id}, writerId=${editedReview.writer.id}, content=\"${editedReview.content}\" }" }

    private fun logAfterDeleteReview(seriesId: Long, reviewId: Long, writer: User) =
        logger.info { "[SeriesReview] deleted: { seriesId=${seriesId}, reviewId=${reviewId}, writerId=${writer.id} }" }
}