package me.loghub.api.mapper.series

import me.loghub.api.dto.common.ContentDTO
import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.dto.series.chapter.SeriesChapterDTO
import me.loghub.api.dto.series.chapter.SeriesChapterDetailDTO
import me.loghub.api.dto.series.chapter.SeriesChapterForEditDTO
import me.loghub.api.entity.series.SeriesChapter

object SeriesChapterMapper {
    fun map(chapter: SeriesChapter) = SeriesChapterDTO(
        id = chapter.persistedId,
        title = chapter.title,
        sequence = chapter.sequence,
        published = chapter.published,
    )

    fun mapDetail(chapter: SeriesChapter, renderedMarkdown: RenderedMarkdownDTO) = SeriesChapterDetailDTO(
        id = chapter.persistedId,
        title = chapter.title,
        content = ContentDTO(
            html = renderedMarkdown.html,
            normalized = chapter.normalizedContent,
        ),
        anchors = renderedMarkdown.anchors,
        sequence = chapter.sequence,
        publishedAt = chapter.publishedAt,
        updatedAt = chapter.updatedAt,
    )

    fun mapForEdit(chapter: SeriesChapter, draft: String?) = SeriesChapterForEditDTO(
        id = chapter.persistedId,
        title = chapter.title,
        content = chapter.content,
        draft = draft,
        sequence = chapter.sequence,
        published = chapter.published,
    )
}