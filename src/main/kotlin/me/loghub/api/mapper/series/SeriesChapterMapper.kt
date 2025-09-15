package me.loghub.api.mapper.series

import me.loghub.api.dto.common.ContentDTO
import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.dto.series.chapter.SeriesChapterDTO
import me.loghub.api.dto.series.chapter.SeriesChapterDetailDTO
import me.loghub.api.dto.series.chapter.SeriesChapterForEditDTO
import me.loghub.api.entity.series.SeriesChapter
import java.time.format.DateTimeFormatter

object SeriesChapterMapper {
    fun map(chapter: SeriesChapter) = SeriesChapterDTO(
        id = chapter.id!!,
        title = chapter.title,
        sequence = chapter.sequence,
        createdAt = chapter.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = chapter.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapDetail(chapter: SeriesChapter, renderedMarkdown: RenderedMarkdownDTO) = SeriesChapterDetailDTO(
        id = chapter.id!!,
        title = chapter.title,
        content = ContentDTO(
            markdown = chapter.content,
            html = renderedMarkdown.html,
        ),
        anchors = renderedMarkdown.anchors,
        sequence = chapter.sequence,
        createdAt = chapter.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = chapter.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapForEdit(chapter: SeriesChapter) = SeriesChapterForEditDTO(
        id = chapter.id!!,
        title = chapter.title,
        content = chapter.content,
        sequence = chapter.sequence,
    )
}