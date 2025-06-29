package kr.loghub.api.service.book

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.book.chapter.BookChapterDetailDTO
import kr.loghub.api.dto.book.chapter.EditBookChapterDTO
import kr.loghub.api.entity.book.BookChapter
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.mapper.book.BookChapterMapper
import kr.loghub.api.repository.book.BookChapterRepository
import kr.loghub.api.repository.book.BookRepository
import kr.loghub.api.service.cache.CacheService
import kr.loghub.api.util.checkPermission
import kr.loghub.api.util.requireNotEquals
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookChapterService(
    private val bookRepository: BookRepository,
    private val bookChapterRepository: BookChapterRepository,
    private val cacheService: CacheService,
) {
    companion object {
        private const val DEFAULT_CHAPTER_TITLE = "새 챕터"
    }

    @Transactional(readOnly = true)
    fun getChapter(bookId: Long, sequence: Int): BookChapterDetailDTO {
        val book = bookRepository.findById(bookId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Book.NOT_FOUND) }
        val chapter = bookChapterRepository.findByBookAndSequence(book, sequence)
            ?: throw EntityNotFoundException(ResponseMessage.Book.Chapter.NOT_FOUND)
        val cachedHTML = cacheService.findOrGenerateMarkdownCache(chapter.content)
        return BookChapterMapper.mapDetail(chapter, cachedHTML)
    }

    @Transactional
    fun createChapter(bookId: Long, writer: User): BookChapter {
        val book = bookRepository.findById(bookId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Book.NOT_FOUND) }

        checkPermission(book.writer == writer) { ResponseMessage.Book.PERMISSION_DENIED }

        val chapterSize = bookChapterRepository.countByBook(book)
        val chapter = BookChapter(
            title = DEFAULT_CHAPTER_TITLE,
            sequence = chapterSize + 1,
            book = book,
        )
        return bookChapterRepository.save(chapter)
    }

    @Transactional
    fun editChapter(
        bookId: Long, sequence: Int, requestBody: EditBookChapterDTO, writer: User
    ): BookChapter {
        val chapter = bookChapterRepository.findAccessibleChapter(bookId, sequence, writer)
        chapter.update(requestBody)
        return chapter
    }

    @Transactional
    fun deleteChapter(bookId: Long, sequence: Int, writer: User) {
        val chapter = bookChapterRepository.findAccessibleChapter(bookId, sequence, writer)
        bookChapterRepository.delete(chapter)
    }

    @Transactional
    fun changeChapterSequence(bookId: Long, sequenceA: Int, sequenceB: Int, writer: User) {
        requireNotEquals("sequenceB", sequenceA, sequenceB) { ResponseMessage.Book.Chapter.SEQUENCE_MUST_BE_DIFF }

        val book = bookRepository.findById(bookId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Book.NOT_FOUND) }

        checkPermission(book.writer == writer) { ResponseMessage.Book.PERMISSION_DENIED }

        val chapterA = bookChapterRepository.findByBookAndSequence(book, sequenceA)
            ?: throw EntityNotFoundException(ResponseMessage.Book.Chapter.NOT_FOUND)
        val chapterB = bookChapterRepository.findByBookAndSequence(book, sequenceB)
            ?: throw EntityNotFoundException(ResponseMessage.Book.Chapter.NOT_FOUND)
        chapterA.updateSequence(sequenceB)
        chapterB.updateSequence(sequenceA)
    }

    private fun BookChapterRepository.findAccessibleChapter(bookId: Long, sequence: Int, writer: User): BookChapter {
        val book = bookRepository.findById(bookId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Book.NOT_FOUND) }
        val chapter = bookChapterRepository.findByBookAndSequence(book, sequence)
            ?: throw EntityNotFoundException(ResponseMessage.Book.Chapter.NOT_FOUND)

        checkPermission(book.writer == writer) { ResponseMessage.Book.PERMISSION_DENIED }

        return chapter
    }
}