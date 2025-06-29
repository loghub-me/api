package kr.loghub.api.repository.book

import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQuery
import jakarta.persistence.EntityManager
import kr.loghub.api.dto.book.BookSort
import kr.loghub.api.entity.book.Book
import kr.loghub.api.entity.book.QBook
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class BookCustomRepository(private val entityManager: EntityManager) {
    fun search(
        query: String,
        sort: BookSort,
        pageable: Pageable,
        username: String? = null
    ): Page<Book> {
        val book = QBook.book
        val fullTextSearch = if (query.isNotBlank()) Expressions.booleanTemplate(
            "ecfts({0}, {1})",
            Expressions.constant(query),
            Expressions.constant("books_search_index")
        ) else null

        val searchQuery = JPAQuery<Book>(entityManager)
            .select(book)
            .from(book)
            .where(
                username?.let { book.writerUsername.eq(it) },
                fullTextSearch
            )
            .orderBy(sort.order)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        val countQuery = JPAQuery<Long>(entityManager)
            .select(book.count())
            .from(book)
            .where(
                username?.let { book.writerUsername.eq(it) },
                fullTextSearch
            )

        val books = searchQuery.fetch()
        val total = countQuery.fetchOne() ?: 0L

        return PageImpl(books, pageable, total)
    }
}