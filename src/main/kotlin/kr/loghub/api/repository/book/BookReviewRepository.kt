package kr.loghub.api.repository.book

import kr.loghub.api.entity.book.Book
import kr.loghub.api.entity.book.BookReview
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface BookReviewRepository : JpaRepository<BookReview, Long> {
    @EntityGraph(attributePaths = ["writer"])
    fun findWithGraphByBook(book: Book, pageable: Pageable): Page<BookReview>

    @EntityGraph(attributePaths = ["writer", "parent", "book"])
    fun findWithGraphByBookIdAndId(bookId: Long, commentId: Long): BookReview?

    fun findByBookAndId(book: Book, id: Long): BookReview?
}