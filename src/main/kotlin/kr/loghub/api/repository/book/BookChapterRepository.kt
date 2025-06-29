package kr.loghub.api.repository.book

import kr.loghub.api.entity.book.Book
import kr.loghub.api.entity.book.BookChapter
import org.springframework.data.jpa.repository.JpaRepository

interface BookChapterRepository : JpaRepository<BookChapter, Long> {
    fun findByBookAndSequence(book: Book, sequence: Int): BookChapter?
    fun countByBook(book: Book): Int
}