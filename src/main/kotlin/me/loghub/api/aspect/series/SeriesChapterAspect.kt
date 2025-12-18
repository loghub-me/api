package me.loghub.api.aspect.series

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.series.SeriesChapter
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import me.loghub.api.repository.user.UserActivityRepository
import me.loghub.api.repository.user.saveActivityIgnoreConflict
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
        if (postedChapter.publishedAt != null) {
            addUserActivityAfterPublishChapter(postedChapter)
        }
        logAfterPostChapter(postedChapter)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesChapterService.importChapter(..)) && args(.., articleId, ..)",
        returning = "importedChapter"
    )
    fun afterImportChapter(articleId: Long, importedChapter: SeriesChapter) {
        logAfterImportChapter(importedChapter, articleId)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesChapterService.editChapter(..)) && args(seriesId, .., writer)",
        returning = "editedChapter"
    )
    fun afterEditChapter(seriesId: Long, writer: User, editedChapter: SeriesChapter) {
        if (editedChapter.published) {
            addUserActivityAfterPublishChapter(editedChapter)
        } else {
            removeUserActivityAfterUnpublishChapter(editedChapter)
        }
        logAfterEditChapter(editedChapter)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesChapterService.deleteChapter(..)) && args(seriesId, sequence, writer)",
    )
    fun afterDeleteChapter(seriesId: Long, sequence: Long, writer: User) =
        logAfterDeleteChapter(seriesId, sequence, writer)

    private fun addUserActivityAfterPublishChapter(postedChapter: SeriesChapter) {
        val publishedAt = requireNotNull(postedChapter.publishedAt)
        userActivityRepository.saveActivityIgnoreConflict(
            UserActivity(
                action = UserActivity.Action.PUBLISH_SERIES_CHAPTER,
                createdAt = publishedAt,
                createdDate = publishedAt.toLocalDate(),
                user = postedChapter.writer,
                series = postedChapter.series,
                seriesChapter = postedChapter
            )
        )
    }

    private fun removeUserActivityAfterUnpublishChapter(editedChapter: SeriesChapter) =
        userActivityRepository.deleteBySeriesChapter(editedChapter)

    fun logAfterPostChapter(chapter: SeriesChapter) =
        logger.info { "[SeriesChapter] posted: { seriesId=${chapter.series.id}, chapterId=${chapter.id}, writerId=${chapter.writer.id}, title=\"${chapter.title}\", published=${chapter.published} }" }

    fun logAfterImportChapter(chapter: SeriesChapter, articleId: Long) =
        logger.info { "[SeriesChapter] imported: { seriesId=${chapter.series.id}, chapterId=${chapter.id}, writerId=${chapter.writer.id}, articleId=${articleId}, title=\"${chapter.title}\", published=${chapter.published} }" }

    fun logAfterEditChapter(chapter: SeriesChapter) =
        logger.info { "[SeriesChapter] edited: { seriesId=${chapter.series.id}, chapterId=${chapter.id}, writerId=${chapter.writer.id}, title=\"${chapter.title}\", published=${chapter.published} }" }

    fun logAfterDeleteChapter(seriesId: Long, sequence: Long, writer: User) =
        logger.info { "[SeriesChapter] deleted: { seriesId=${seriesId}, chapterSequence=${sequence}, writerId=${writer.id} }" }
}