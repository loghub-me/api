package me.loghub.api.aspect.logging.series

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class SeriesLoggingAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesService.postSeries(..)) && args(.., writer)",
        returning = "series"
    )
    fun afterPostSeries(writer: User, series: Series) =
        logger.info { "[Series] posted: { seriesId=${series.id}, writerId=${writer.id}, title=\"${series.title}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesService.editSeries(..)) && args(.., writer)",
        returning = "series"
    )
    fun afterEditSeries(writer: User, series: Series) =
        logger.info { "[Series] edited: { seriesId=${series.id}, writerId=${writer.id}, title=\"${series.title}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesService.deleteSeries(..)) && args(seriesId, writer)"
    )
    fun afterDeleteSeries(seriesId: Long, writer: User) =
        logger.info { "[Series] deleted: { seriesId=${seriesId}, writerId=${writer.id} }" }
}
