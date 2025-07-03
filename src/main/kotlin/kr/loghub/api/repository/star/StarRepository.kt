package kr.loghub.api.repository.star

import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.book.Book
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.star.Star
import kr.loghub.api.entity.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface StarRepository : JpaRepository<Star, Long> {
    @EntityGraph(attributePaths = ["article", "book", "question", "user"])
    fun findAllByUser(user: User, pageable: Pageable): Page<Star>

    // -----------[Article]-----------
    fun existsByArticleIdAndUser(articleId: Long, user: User): Boolean
    fun existsByArticleAndUser(article: Article, user: User): Boolean

    @EntityGraph(attributePaths = ["article", "user"])
    fun deleteByArticleIdAndUser(articleId: Long, user: User): Int

    // -----------[Book]-----------
    fun existsByBookIdAndUser(bookId: Long, user: User): Boolean
    fun existsByBookAndUser(book: Book, user: User): Boolean

    @EntityGraph(attributePaths = ["book", "user"])
    fun deleteByBookIdAndUser(bookId: Long, user: User): Int

    // -----------[Question]-----------
    fun existsByQuestionIdAndUser(questionId: Long, user: User): Boolean
    fun existsByQuestionAndUser(question: Question, user: User): Boolean

    @EntityGraph(attributePaths = ["question", "user"])
    fun deleteByQuestionIdAndUser(questionId: Long, user: User): Int
}