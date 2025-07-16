package kr.loghub.api.mapper.series

import kr.loghub.api.dto.series.chapter.SeriesChapterDTO
import kr.loghub.api.dto.series.chapter.SeriesChapterDetailDTO
import kr.loghub.api.dto.common.ContentDTO
import kr.loghub.api.entity.series.SeriesChapter
import java.time.format.DateTimeFormatter

object SeriesChapterMapper {
    fun map(chapter: SeriesChapter) = SeriesChapterDTO(
        id = chapter.id!!,
        title = chapter.title,
        sequence = chapter.sequence,
        createdAt = chapter.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = chapter.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapDetail(chapter: SeriesChapter, contentHTML: String) = SeriesChapterDetailDTO(
        id = chapter.id!!,
        title = chapter.title,
        content = ContentDTO(
            markdown = chapter.content,
            html = contentHTML,
        ),
        sequence = chapter.sequence,
        createdAt = chapter.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = chapter.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )
}