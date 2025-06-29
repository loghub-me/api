package kr.loghub.api.service.book

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.entity.star.Star
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.repository.book.BookRepository
import kr.loghub.api.repository.star.StarRepository
import kr.loghub.api.service.star.StarService
import kr.loghub.api.util.checkAlreadyExists
import kr.loghub.api.util.checkExists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookStarService(
    private val starRepository: StarRepository,
    private val bookRepository: BookRepository,
) : StarService {
    @Transactional(readOnly = true)
    override fun existsStar(bookId: Long, user: User): Boolean =
        starRepository.existsByBookIdAndUser(bookId, user)

    @Transactional
    override fun addStar(bookId: Long, user: User): Star {
        val book = bookRepository.findById(bookId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Book.NOT_FOUND) }

        checkAlreadyExists(starRepository.existsByBookAndUser(book, user)) {
            ResponseMessage.Star.ALREADY_EXISTS
        }

        book.incrementStarCount()
        return starRepository.save(Star(user = user, book = book, target = Star.Target.BOOK))
    }

    @Transactional
    override fun removeStar(bookId: Long, user: User) {
        val affectedRows = starRepository.deleteByBookIdAndUser(bookId, user)

        checkExists(affectedRows > 0) { ResponseMessage.Star.NOT_FOUND }

        bookRepository.decrementStarCount(bookId)
    }
}