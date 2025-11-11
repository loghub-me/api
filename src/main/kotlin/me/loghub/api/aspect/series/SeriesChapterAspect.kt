package me.loghub.api.aspect.series

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.series.SeriesChapter
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class SeriesChapterAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesChapterService.createChapter(..))",
        returning = "postedChapter"
    )
    fun afterCreateChapter(postedChapter: SeriesChapter) =
        logger.info { "[SeriesChapter] created: { seriesId=${postedChapter.series.id}, chapterId=${postedChapter.id}, writerId=${postedChapter.writer.id}, title=\"${postedChapter.title}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesChapterService.importChapter(..)) && args(.., articleId, ..)",
        returning = "importedChapter"
    )
    fun afterImportChapter(articleId: Long, importedChapter: SeriesChapter) =
        logger.info { "[SeriesChapter] imported: { seriesId=${importedChapter.series.id}, chapterId=${importedChapter.id}, writerId=${importedChapter.writer.id}, articleId=${articleId}, title=\"${importedChapter.title}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesChapterService.editChapter(..)) && args(seriesId, .., writer)",
        returning = "chapter"
    )
    fun afterEditChapter(seriesId: Long, writer: User, chapter: SeriesChapter) =
        logger.info { "[SeriesChapter] edited: { seriesId=${seriesId}, chapterId=${chapter.id}, writerId=${writer.id}, title=\"${chapter.title}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.series.SeriesChapterService.deleteChapter(..)) && args(seriesId, sequence, writer)",
    )
    fun afterDeleteChapter(seriesId: Long, sequence: Long, writer: User) =
        logger.info { "[SeriesChapter] deleted: { seriesId=${seriesId}, chapterSequence=${sequence}, writerId=${writer.id} }" }
}