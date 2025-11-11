package me.loghub.api.aspect.series

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import me.loghub.api.repository.user.UserActivityRepository
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class SeriesAspect(
    private val userActivityRepository: UserActivityRepository,
) {
    private companion object {
        val logger = KotlinLogging.logger { };
    }

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
        logAfterDeleteSeries(seriesId, writer)
    }

    private fun addUserActivityAfterPostSeries(postedSeries: Series) {
        val activity = UserActivity(
            action = UserActivity.Action.POST_SERIES,
            user = postedSeries.writer,
            series = postedSeries
        )
        userActivityRepository.save(activity)
    }

    private fun logAfterPostSeries(postedSeries: Series) =
        logger.info { "[Series] posted: { seriesId=${postedSeries.id}, writerId=${postedSeries.writer.id}, title=\"${postedSeries.title}\" }" }

    private fun logAfterEditSeries(editedSeries: Series) =
        logger.info { "[Series] edited: { seriesId=${editedSeries.id}, writerId=${editedSeries.writer.id}, title=\"${editedSeries.title}\" }" }

    private fun logAfterDeleteSeries(seriesId: Long, writer: User) =
        logger.info { "[Series] deleted: { seriesId=${seriesId}, writerId=${writer.id} }" }
}