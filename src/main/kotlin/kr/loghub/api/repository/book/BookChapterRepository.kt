package kr.loghub.api.repository.book

import kr.loghub.api.entity.book.Book
import kr.loghub.api.entity.book.BookChapter
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BookChapterRepository : JpaRepository<BookChapter, Long> {
    companion object {
        const val SELECT_CHAPTER = "SELECT bc FROM BookChapter bc"
        const val BY_BOOK_ID = "bc.book.id = :bookId"
    }

    @Query("$SELECT_CHAPTER WHERE $BY_BOOK_ID AND bc.sequence = :sequence")
    @EntityGraph(attributePaths = ["writer"])
    fun findByBookIdAndSequence(bookId: Long, sequence: Int): BookChapter?

    fun findAllByBookIdAndSequenceGreaterThanOrderBySequenceAsc(bookId: Long, sequence: Int): List<BookChapter>

    fun countByBook(book: Book): Int
}