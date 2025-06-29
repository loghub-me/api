package kr.loghub.api.mapper.book

import kr.loghub.api.dto.book.chapter.BookChapterDTO
import kr.loghub.api.dto.book.chapter.BookChapterDetailDTO
import kr.loghub.api.dto.common.ContentDTO
import kr.loghub.api.entity.book.BookChapter
import java.time.format.DateTimeFormatter

object BookChapterMapper {
    fun map(chapter: BookChapter) = BookChapterDTO(
        id = chapter.id!!,
        title = chapter.title,
        sequence = chapter.sequence,
        createdAt = chapter.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = chapter.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapDetail(chapter: BookChapter, contentHTML: String) = BookChapterDetailDTO(
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