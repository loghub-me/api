package kr.loghub.api.repository.user

import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.series.Series
import kr.loghub.api.entity.user.User
import kr.loghub.api.entity.user.UserStar
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface UserStarRepository : JpaRepository<UserStar, Long> {
    @EntityGraph(attributePaths = ["article", "series", "question"])
    fun findAllByUser(user: User, pageable: Pageable): Page<UserStar>

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