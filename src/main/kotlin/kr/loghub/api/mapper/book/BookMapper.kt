package kr.loghub.api.mapper.book

import kr.loghub.api.dto.book.BookDTO
import kr.loghub.api.dto.book.BookDetailDTO
import kr.loghub.api.dto.book.BookStatsDTO
import kr.loghub.api.entity.book.Book
import kr.loghub.api.entity.book.BookStats
import kr.loghub.api.mapper.user.UserMapper
import java.time.format.DateTimeFormatter

object BookMapper {
    fun map(book: Book) = BookDTO(
        id = book.id!!,
        slug = book.slug,
        title = book.title,
        thumbnail = book.thumbnail,
        writerUsername = book.writerUsername,
        stats = mapStats(book.stats),
        topics = book.topicsFlat,
        createdAt = book.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = book.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapDetail(book: Book) = BookDetailDTO(
        id = book.id!!,
        slug = book.slug,
        title = book.title,
        content = book.content,
        thumbnail = book.thumbnail,
        writer = UserMapper.map(book.writer),
        stats = mapStats(book.stats),
        topics = book.topicsFlat,
        chapters = book.chapters.map(BookChapterMapper::map),
        createdAt = book.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = book.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    private fun mapStats(book: BookStats) = BookStatsDTO(book.starCount, book.reviewCount)
}