package kr.loghub.api.service.book

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.book.BookDTO
import kr.loghub.api.dto.book.BookDetailDTO
import kr.loghub.api.dto.book.BookSort
import kr.loghub.api.dto.book.PostBookDTO
import kr.loghub.api.entity.book.Book
import kr.loghub.api.entity.book.BookStats
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.mapper.book.BookMapper
import kr.loghub.api.repository.book.BookCustomRepository
import kr.loghub.api.repository.book.BookRepository
import kr.loghub.api.repository.topic.TopicRepository
import kr.loghub.api.service.cache.CacheService
import kr.loghub.api.util.checkField
import kr.loghub.api.util.checkPermission
import kr.loghub.api.util.toSlug
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val bookCustomRepository: BookCustomRepository,
    private val topicRepository: TopicRepository,
    private val cacheService: CacheService,
) {
    companion object {
        private const val PAGE_SIZE = 20
    }

    @Transactional(readOnly = true)
    fun searchBooks(query: String, sort: BookSort, page: Int): Page<BookDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        return bookCustomRepository.search(
            query = query.trim(),
            sort = sort,
            pageable = PageRequest.of(page - 1, PAGE_SIZE)
        ).map(BookMapper::map)
    }

    @Transactional(readOnly = true)
    fun getTrendingBooks(): List<BookDTO> = bookRepository.findAll(
        PageRequest.of(
            0,
            PAGE_SIZE,
            Sort.by("${Book::stats.name}.${BookStats::trendingScore.name}").descending(),
        )
    ).toList().map(BookMapper::map)

    @Transactional(readOnly = true)
    fun getBook(username: String, slug: String): BookDetailDTO =
        bookRepository.findWithWriterByCompositeKey(username, slug)
            ?.let { BookMapper.mapDetail(it) }
            ?: throw EntityNotFoundException(ResponseMessage.Book.NOT_FOUND)

    @Transactional
    fun postBook(requestBody: PostBookDTO, writer: User): Book {
        val slug = generateUniqueSlug(writer.username, requestBody.title)
        val book = requestBody.toEntity(slug, writer)
        return bookRepository.save(book)
    }

    @Transactional
    fun editBook(bookId: Long, requestBody: PostBookDTO, writer: User): Book {
        val book = bookRepository.findWithWriterById(bookId)
            ?: throw EntityNotFoundException(ResponseMessage.Book.NOT_FOUND)

        checkPermission(book.writer == writer) { ResponseMessage.Book.PERMISSION_DENIED }

        val slug = generateUniqueSlug(writer.username, requestBody.title)

        book.update(requestBody)
        book.updateSlug(slug)
        return book
    }

    @Transactional
    fun removeBook(bookId: Long, writer: User) {
        val book = bookRepository.findWithWriterById(bookId)
            ?: throw EntityNotFoundException(ResponseMessage.Book.NOT_FOUND)

        checkPermission(book.writer == writer) { ResponseMessage.Book.PERMISSION_DENIED }

        bookRepository.delete(book)
    }

    private fun generateUniqueSlug(username: String, title: String): String {
        var slug = title.toSlug()
        while (bookRepository.existsByCompositeKey(username, slug)) {
            slug = "$slug-${UUID.randomUUID()}"
        }
        return slug
    }
}