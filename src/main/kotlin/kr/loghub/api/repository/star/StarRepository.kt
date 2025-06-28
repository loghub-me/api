package kr.loghub.api.repository.star

import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.star.Star
import kr.loghub.api.entity.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface StarRepository : JpaRepository<Star, Long> {
    // -----------[Article]-----------

    @EntityGraph(attributePaths = ["article", "question", "user"])
    fun findByUser(user: User, pageable: Pageable): Page<Star>

    fun existsByArticleAndUserId(article: Article, userId: Long): Boolean

    fun existsByArticleIdAndUserId(articleId: Long, userId: Long): Boolean

    @EntityGraph(attributePaths = ["article", "user"])
    fun deleteByArticleIdAndUserId(articleId: Long, userId: Long): Int

    // -----------[Question]-----------

    fun existsByQuestionAndUserId(question: Question, userId: Long): Boolean

    fun existsByQuestionIdAndUserId(questionId: Long, userId: Long): Boolean

    @EntityGraph(attributePaths = ["question", "user"])
    fun deleteByQuestionIdAndUserId(questionId: Long, userId: Long): Int
}