package me.loghub.api.aspect.logging.series

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.series.SeriesReview
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class SeriesReviewLoggingAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesReviewService.postReview(..)) && args(seriesId, .., writer)",
        returning = "review"
    )
    fun afterPostReview(seriesId: Long, writer: User, review: SeriesReview) =
        logger.info { "[SeriesReview] posted: { seriesId=${seriesId}, reviewId=${review.id}, writerId=${writer.id}, content=\"${review.content}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesReviewService.editReview(..)) && args(seriesId, .., writer)",
        returning = "review"
    )
    fun afterEditReview(seriesId: Long, writer: User, review: SeriesReview) =
        logger.info { "[SeriesReview] edited: { seriesId=${seriesId}, reviewId=${review.id}, writerId=${writer.id}, content=\"${review.content}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesReviewService.deleteReview(..)) && args(seriesId, reviewId, writer)"
    )
    fun afterDeleteReview(seriesId: Long, reviewId: Long, writer: User) =
        logger.info { "[SeriesReview] deleted: { seriesId=${seriesId}, reviewId=${reviewId}, writerId=${writer.id} }" }
}