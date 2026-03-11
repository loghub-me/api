package me.loghub.api.aspect.series

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.series.SeriesReview
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class SeriesReviewAspect {
    private companion object {
        val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesReviewService.postReview(..))",
        returning = "postedReview"
    )
    fun afterPostReview(postedReview: SeriesReview) {
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
        logAfterDeleteReview(seriesId, reviewId, writer)
    }

    private fun logAfterPostReview(review: SeriesReview) =
        logger.info { "[SeriesReview] posted: { seriesId=${review.series.id}, reviewId=${review.id}, writerId=${review.writer.id}, content=\"${review.content}\" }" }

    private fun logAfterEditReview(review: SeriesReview) =
        logger.info { "[SeriesReview] edited: { seriesId=${review.series.id}, reviewId=${review.id}, writerId=${review.writer.id}, content=\"${review.content}\" }" }

    private fun logAfterDeleteReview(seriesId: Long, reviewId: Long, writer: User) =
        logger.info { "[SeriesReview] deleted: { seriesId=${seriesId}, reviewId=${reviewId}, writerId=${writer.id} }" }
}
