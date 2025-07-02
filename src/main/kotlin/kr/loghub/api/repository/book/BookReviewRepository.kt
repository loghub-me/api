package kr.loghub.api.repository.book

import kr.loghub.api.entity.book.Book
import kr.loghub.api.entity.book.BookReview
import kr.loghub.api.entity.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BookReviewRepository : JpaRepository<BookReview, Long> {
    companion object {
        const val SELECT_REVIEW = "SELECT br FROM BookReview br"
        const val BY_BOOK_ID = "br.book.id = :bookId"
    }

    @EntityGraph(attributePaths = ["writer"])
    @Query("$SELECT_REVIEW WHERE $BY_BOOK_ID")
    fun findByBookId(bookId: Long, pageable: Pageable): Page<BookReview>

    @EntityGraph(attributePaths = ["writer", "book"])
    fun findWithGraphByBookIdAndId(bookId: Long, commentId: Long): BookReview?

    fun existsByBookAndWriter(book: Book, writer: User): Boolean
}