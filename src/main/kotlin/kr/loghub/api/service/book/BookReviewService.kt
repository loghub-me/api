package kr.loghub.api.service.book

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.book.review.BookReviewDTO
import kr.loghub.api.dto.book.review.PostBookReviewDTO
import kr.loghub.api.entity.book.BookReview
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.mapper.book.BookReviewMapper
import kr.loghub.api.repository.book.BookRepository
import kr.loghub.api.repository.book.BookReviewRepository
import kr.loghub.api.util.checkAlreadyExists
import kr.loghub.api.util.checkPermission
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookReviewService(
    private val bookRepository: BookRepository,
    private val bookReviewRepository: BookReviewRepository,
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }

    @Transactional(readOnly = true)
    fun getReviews(bookId: Long, page: Int): Page<BookReviewDTO> =
        bookReviewRepository.findByBookId(
            bookId = bookId,
            pageable = PageRequest.of(
                page - 1,
                DEFAULT_PAGE_SIZE,
                Sort.by(BookReview::createdAt.name).descending()
            ),
        ).map(BookReviewMapper::map)

    @Transactional
    fun postReview(bookId: Long, requestBody: PostBookReviewDTO, writer: User): BookReview {
        val book = bookRepository.findById(bookId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Book.NOT_FOUND) }

        checkAlreadyExists(bookReviewRepository.existsByBookAndWriter(book, writer)) {
            ResponseMessage.Book.Review.ALREADY_EXISTS
        }

        val comment = requestBody.toEntity(book, writer)
        book.incrementReviewCount()
        return bookReviewRepository.save(comment)
    }

    @Transactional
    fun removeReview(bookId: Long, commentId: Long, writer: User) {
        val comment = bookReviewRepository.findWithGraphByBookIdAndId(bookId, commentId)
            ?: throw EntityNotFoundException(ResponseMessage.Book.Review.NOT_FOUND)

        checkPermission(comment.writer == writer) { ResponseMessage.Book.Review.PERMISSION_DENIED }

        comment.book.decrementReviewCount()
        bookReviewRepository.delete(comment)
    }
}