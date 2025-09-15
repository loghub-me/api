package me.loghub.api.service.series

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.series.chapter.EditSeriesChapterDTO
import me.loghub.api.dto.series.chapter.SeriesChapterDetailDTO
import me.loghub.api.dto.series.chapter.SeriesChapterForEditDTO
import me.loghub.api.dto.series.chapter.UpdateSeriesChapterSequenceDTO
import me.loghub.api.entity.series.SeriesChapter
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.series.SeriesChapterMapper
import me.loghub.api.repository.series.SeriesChapterRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.service.common.CacheService
import me.loghub.api.util.checkField
import me.loghub.api.util.checkPermission
import me.loghub.api.util.orElseThrowNotFound
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SeriesChapterService(
    private val seriesRepository: SeriesRepository,
    private val seriesChapterRepository: SeriesChapterRepository,
    private val cacheService: CacheService,
) {
    private companion object {
        private const val DEFAULT_CHAPTER_TITLE = "새 챕터"
    }

    @Transactional(readOnly = true)
    fun getChapter(seriesId: Long, sequence: Int): SeriesChapterDetailDTO {
        val series = seriesRepository.getReferenceById(seriesId)
        val chapter = seriesChapterRepository.findWithWriterBySeriesAndSequence(series, sequence)
            ?: throw EntityNotFoundException(ResponseMessage.Series.Chapter.NOT_FOUND)
        val renderedMarkdown = cacheService.findOrGenerateMarkdownCache(chapter.content)
        return SeriesChapterMapper.mapDetail(chapter, renderedMarkdown)
    }

    @Transactional(readOnly = true)
    fun getChapterForEdit(seriesId: Long, sequence: Int, writer: User): SeriesChapterForEditDTO {
        val series = seriesRepository.getReferenceById(seriesId)
        val chapter = seriesChapterRepository.findWithWriterBySeriesAndSequence(series, sequence)
            ?: throw EntityNotFoundException(ResponseMessage.Series.Chapter.NOT_FOUND)

        checkPermission(chapter.writer == writer) { ResponseMessage.Series.PERMISSION_DENIED }

        return SeriesChapterMapper.mapForEdit(chapter)
    }

    @Transactional
    fun createChapter(seriesId: Long, writer: User): SeriesChapter {
        val series = seriesRepository.findById(seriesId)
            .orElseThrowNotFound { ResponseMessage.Series.NOT_FOUND }

        checkPermission(series.writer == writer) { ResponseMessage.Series.PERMISSION_DENIED }

        val chapterSize = seriesChapterRepository.countBySeries(series)
        val chapter = SeriesChapter(
            title = DEFAULT_CHAPTER_TITLE,
            sequence = chapterSize + 1,
            series = series,
            writer = writer,
        )
        return seriesChapterRepository.save(chapter)
    }

    @Transactional
    fun editChapter(
        seriesId: Long, sequence: Int, requestBody: EditSeriesChapterDTO, writer: User
    ): SeriesChapter {
        val series = seriesRepository.getReferenceById(seriesId)
        val chapter = seriesChapterRepository.findWithWriterBySeriesAndSequence(series, sequence)
            ?: throw EntityNotFoundException(ResponseMessage.Series.Chapter.NOT_FOUND)

        checkPermission(chapter.writer == writer) { ResponseMessage.Series.PERMISSION_DENIED }

        chapter.update(requestBody)
        return chapter
    }

    @Transactional
    fun deleteChapter(seriesId: Long, sequence: Int, writer: User) {
        val series = seriesRepository.getReferenceById(seriesId)
        val chapter = seriesChapterRepository.findWithWriterBySeriesAndSequence(series, sequence)
            ?: throw EntityNotFoundException(ResponseMessage.Series.Chapter.NOT_FOUND)
        val chaptersToShift =
            seriesChapterRepository.findAllBySeriesIdAndSequenceGreaterThanOrderBySequenceAsc(seriesId, sequence)

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