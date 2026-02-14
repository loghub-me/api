package me.loghub.api.service.series

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.series.chapter.EditSeriesChapterDTO
import me.loghub.api.dto.series.chapter.SeriesChapterDetailDTO
import me.loghub.api.dto.series.chapter.SeriesChapterForEditDTO
import me.loghub.api.dto.series.chapter.UpdateSeriesChapterSequenceDTO
import me.loghub.api.entity.series.SeriesChapter
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.lib.redis.key.series.SeriesChapterDraftRedisKey
import me.loghub.api.mapper.series.SeriesChapterMapper
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.series.SeriesChapterRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.service.common.MarkdownService
import me.loghub.api.util.*
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SeriesChapterService(
    private val seriesRepository: SeriesRepository,
    private val seriesChapterRepository: SeriesChapterRepository,
    private val articleRepository: ArticleRepository,
    private val markdownService: MarkdownService,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private companion object {
        private const val DEFAULT_CHAPTER_TITLE = "새 챕터"
        private const val DEFAULT_CHAPTER_CONTENT = ""
        private const val MAX_CHAPTER_SIZE = 20
    }

    @Transactional(readOnly = true)
    fun getChapter(seriesId: Long, sequence: Int): SeriesChapterDetailDTO {
        val series = seriesRepository.getReferenceById(seriesId)
        val chapter = seriesChapterRepository.findWithWriterBySeriesAndSequence(series, sequence)
            ?: throw EntityNotFoundException(ResponseMessage.Series.Chapter.NOT_FOUND)

        checkPublished(chapter.published) { ResponseMessage.Series.NOT_FOUND }

        val renderedMarkdown = markdownService.findOrGenerateMarkdownCache(chapter.content)
        return SeriesChapterMapper.mapDetail(chapter, renderedMarkdown)
    }

    @Transactional(readOnly = true)
    fun getChapterForEdit(seriesId: Long, chapterId: Long, writer: User): SeriesChapterForEditDTO {
        val series = seriesRepository.getReferenceById(seriesId)
        val chapter = seriesChapterRepository.findWithWriterBySeriesAndId(series, chapterId)
            ?: throw EntityNotFoundException(ResponseMessage.Series.Chapter.NOT_FOUND)

        checkPermission(chapter.writer == writer) { ResponseMessage.Series.PERMISSION_DENIED }

        val draftRedisKey = SeriesChapterDraftRedisKey(seriesId, chapter.id!!)
        val draft = redisTemplate.opsForValue().get(draftRedisKey)
        return SeriesChapterMapper.mapForEdit(chapter, draft)
    }

    @Transactional
    fun createChapter(seriesId: Long, writer: User): SeriesChapter {
        val series = seriesRepository.findById(seriesId)
            .orElseThrowNotFound { ResponseMessage.Series.NOT_FOUND }

        checkPermission(series.writer == writer) { ResponseMessage.Series.PERMISSION_DENIED }
        checkConflict(series.stats.chapterCount >= MAX_CHAPTER_SIZE) { ResponseMessage.Series.Chapter.SIZE_LIMIT_EXCEEDED }

        val newChapterCount = seriesRepository.increaseAndGetChapterCount(seriesId, MAX_CHAPTER_SIZE)
        val chapter = SeriesChapter(
            title = DEFAULT_CHAPTER_TITLE,
            content = DEFAULT_CHAPTER_CONTENT,
            normalizedContent = DEFAULT_CHAPTER_CONTENT,
            sequence = newChapterCount,
            series = series,
            writer = writer,
            published = false,
        )
        return seriesChapterRepository.save(chapter)
    }

    @Transactional
    fun importChapter(seriesId: Long, articleId: Long, writer: User): SeriesChapter {
        val series = seriesRepository.findById(seriesId)
            .orElseThrowNotFound { ResponseMessage.Series.NOT_FOUND }

        checkPermission(series.writer == writer) { ResponseMessage.Series.PERMISSION_DENIED }
        checkConflict(series.stats.chapterCount >= MAX_CHAPTER_SIZE) { ResponseMessage.Series.Chapter.SIZE_LIMIT_EXCEEDED }

        val article = articleRepository.findWithWriterById(articleId)
            ?: throw EntityNotFoundException(ResponseMessage.Article.NOT_FOUND)

        checkPermission(article.writer == writer) { ResponseMessage.Article.PERMISSION_DENIED }

        val newChapterCount = seriesRepository.increaseAndGetChapterCount(seriesId, MAX_CHAPTER_SIZE)
        val chapter = SeriesChapter(
            title = article.title,
            content = article.content,
            normalizedContent = article.normalizedContent,
            sequence = newChapterCount,
            series = series,
            writer = writer,
            published = false,
        )
        return seriesChapterRepository.save(chapter)
    }

    @Transactional
    fun editChapter(seriesId: Long, chapterId: Long, requestBody: EditSeriesChapterDTO, writer: User): SeriesChapter {
        val series = seriesRepository.getReferenceById(seriesId)
        val chapter = seriesChapterRepository.findWithWriterBySeriesAndId(series, chapterId)
            ?: throw EntityNotFoundException(ResponseMessage.Series.Chapter.NOT_FOUND)

        checkPermission(chapter.writer == writer) { ResponseMessage.Series.PERMISSION_DENIED }

        val normalizedContent = markdownService.normalizeMarkdown(requestBody.content)

        chapter.update(requestBody, normalizedContent)
        if (requestBody.published) chapter.publish() else chapter.unpublish()

        return chapter
    }

    @Transactional
    fun deleteChapter(seriesId: Long, chapterId: Long, writer: User) {
        val series = seriesRepository.getReferenceById(seriesId)
        val chapter = seriesChapterRepository.findWithWriterBySeriesAndId(series, chapterId)
            ?: throw EntityNotFoundException(ResponseMessage.Series.Chapter.NOT_FOUND)
        val chaptersToShift = seriesChapterRepository.findAllBySeriesIdAndSequenceGreaterThanOrderBySequenceAsc(
            seriesId,
            chapter.sequence
        )

        checkPermission(chapter.writer == writer) { ResponseMessage.Series.PERMISSION_DENIED }

        seriesChapterRepository.delete(chapter)
        chaptersToShift.forEach { it.updateSequence(it.sequence - 1) }
    }

    @Transactional
    fun updateChapterSequence(seriesId: Long, requestBody: UpdateSeriesChapterSequenceDTO, writer: User) {
        val series = seriesRepository.getReferenceById(seriesId)
        val chapters = seriesChapterRepository.findWithWriterAllBySeriesOrderBySequenceAsc(series)
        val sequences = requestBody.sequences

        checkField(
            UpdateSeriesChapterSequenceDTO::sequences.name,
            sequences.size == chapters.size
        ) { ResponseMessage.Series.Chapter.INVALID_SEQUENCE }
        checkField(
            UpdateSeriesChapterSequenceDTO::sequences.name,
            sequences.distinct().size == chapters.size
        ) { ResponseMessage.Series.Chapter.DUPLICATED_SEQUENCE }
        checkField(
            UpdateSeriesChapterSequenceDTO::sequences.name,
            sequences.all { it in 1..chapters.size }
        ) { ResponseMessage.Series.Chapter.OUT_OF_BOUNDS_SEQUENCE }

        for (i in 0..<chapters.size) {
            checkPermission(chapters[i].writer == writer) { ResponseMessage.Series.PERMISSION_DENIED }
            if (chapters[i].sequence != sequences[i]) {
                chapters[i].updateSequence(sequences[i])
            }
        }
    }
}