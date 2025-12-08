package me.loghub.api.aspect.series

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.series.SeriesChapter
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import me.loghub.api.repository.user.UserActivityRepository
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class SeriesChapterAspect(
    private val userActivityRepository: UserActivityRepository,
) {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesChapterService.createChapter(..))",
        returning = "postedChapter"
    )
    fun afterCreateChapter(postedChapter: SeriesChapter) {
        addUserActivityAfterPostChapter(postedChapter)
        logAfterPostChapter(postedChapter)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesChapterService.importChapter(..)) && args(.., articleId, ..)",
        returning = "importedChapter"
    )
    fun afterImportChapter(articleId: Long, importedChapter: SeriesChapter) {
        addUserActivityAfterPostChapter(importedChapter)
        logAfterImportChapter(importedChapter, articleId)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesChapterService.editChapter(..)) && args(seriesId, .., writer)",
        returning = "chapter"
    )
    fun afterEditChapter(seriesId: Long, writer: User, chapter: SeriesChapter) =
        logAfterEditChapter(chapter)

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesChapterService.deleteChapter(..)) && args(seriesId, sequence, writer)",
    )
    fun afterDeleteChapter(seriesId: Long, sequence: Long, writer: User) =
        logAfterDeleteChapter(seriesId, sequence, writer)

    private fun addUserActivityAfterPostChapter(postedChapter: SeriesChapter) {
        val activity = UserActivity(
            action = UserActivity.Action.POST_SERIES_CHAPTER,
            user = postedChapter.writer,
            series = postedChapter.series,
            seriesChapter = postedChapter
        )
        userActivityRepository.save(activity)
    }

    fun logAfterPostChapter(postedChapter: SeriesChapter) =
        logger.info { "[SeriesChapter] posted: { seriesId=${postedChapter.series.id}, chapterId=${postedChapter.id}, writerId=${postedChapter.writer.id}, title=\"${postedChapter.title}\" }" }

    fun logAfterImportChapter(importedChapter: SeriesChapter, articleId: Long) =
        logger.info { "[SeriesChapter] imported: { seriesId=${importedChapter.series.id}, chapterId=${importedChapter.id}, writerId=${importedChapter.writer.id}, articleId=${articleId}, title=\"${importedChapter.title}\" }" }

    fun logAfterEditChapter(editedChapter: SeriesChapter) =
        logger.info { "[SeriesChapter] edited: { seriesId=${editedChapter.series.id}, chapterId=${editedChapter.id}, writerId=${editedChapter.writer.id}, title=\"${editedChapter.title}\" }" }

    fun logAfterDeleteChapter(seriesId: Long, sequence: Long, writer: User) =
        logger.info { "[SeriesChapter] deleted: { seriesId=${seriesId}, chapterSequence=${sequence}, writerId=${writer.id} }" }
}