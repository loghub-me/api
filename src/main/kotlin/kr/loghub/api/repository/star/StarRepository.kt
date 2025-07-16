package kr.loghub.api.repository.star

import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.series.Series
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.star.Star
import kr.loghub.api.entity.user.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface StarRepository : JpaRepository<Star, Long> {
    @EntityGraph(attributePaths = ["article", "series", "question", "user"])
    fun findAllByUser(user: User, pageable: Pageable): Page<Star>

    // -----------[Article]-----------
    fun existsByArticleIdAndUser(articleId: Long, user: User): Boolean
    fun existsByArticleAndUser(article: Article, user: User): Boolean

    @EntityGraph(attributePaths = ["article", "user"])
    fun deleteByArticleIdAndUser(articleId: Long, user: User): Int

    // -----------[Series]-----------
    fun existsBySeriesIdAndUser(seriesId: Long, user: User): Boolean
    fun existsBySeriesAndUser(series: Series, user: User): Boolean

    @EntityGraph(attributePaths = ["series", "user"])
    fun deleteBySeriesIdAndUser(seriesId: Long, user: User): Int

    // -----------[Question]-----------
    fun existsByQuestionIdAndUser(questionId: Long, user: User): Boolean
    fun existsByQuestionAndUser(question: Question, user: User): Boolean

    @EntityGraph(attributePaths = ["question", "user"])
    fun deleteByQuestionIdAndUser(questionId: Long, user: User): Int
}