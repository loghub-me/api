package me.loghub.api.repository.user

import me.loghub.api.dto.user.star.UserStarDTO
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserStar
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserStarRepository : JpaRepository<UserStar, Long> {
    private companion object {
        const val SELECT_DTO = """
            SELECT new me.loghub.api.dto.user.star.UserStarDTO(
                us.id,
                CASE
                    WHEN us.target = 'ARTICLE' THEN us.article.slug
                    WHEN us.target = 'SERIES' THEN us.series.slug
                    WHEN us.target = 'QUESTION' THEN us.question.slug
                END,
                CASE
                    WHEN us.target = 'ARTICLE' THEN us.article.title
                    WHEN us.target = 'SERIES' THEN us.series.title
                    WHEN us.target = 'QUESTION' THEN us.question.title
                END,
                new me.loghub.api.dto.user.UserDTO(
                    CASE
                        WHEN us.target = 'ARTICLE' THEN us.article.writer.id
                        WHEN us.target = 'SERIES' THEN us.series.writer.id
                        WHEN us.target = 'QUESTION' THEN us.question.writer.id
                    END,
                    CASE
                        WHEN us.target = 'ARTICLE' THEN us.article.writerUsername
                        WHEN us.target = 'SERIES' THEN us.series.writerUsername
                        WHEN us.target = 'QUESTION' THEN us.question.writerUsername
                    END
                ),
                CASE
                    WHEN us.target = 'ARTICLE' THEN us.article.topicsFlat
                    WHEN us.target = 'SERIES' THEN us.series.topicsFlat
                    WHEN us.target = 'QUESTION' THEN us.question.topicsFlat
                END,
                us.target
            )
            FROM UserStar us
            LEFT JOIN us.article
            LEFT JOIN us.series
            LEFT JOIN us.question
        """
        const val BY_USER = "us.user = :user"
        const val CREATED_AT_DESC = "us.createdAt DESC"
    }

    @Query("$SELECT_DTO WHERE $BY_USER ORDER BY $CREATED_AT_DESC")
    fun findDTOsByUser(user: User, pageable: Pageable): Page<UserStarDTO>

    // -----------[Article]-----------
    fun existsByArticleAndUser(article: Article, user: User): Boolean

    @EntityGraph(attributePaths = ["article", "user"])
    fun deleteByArticleAndUser(article: Article, user: User): Int

    // -----------[Series]-----------
    fun existsBySeriesAndUser(series: Series, user: User): Boolean

    @EntityGraph(attributePaths = ["series", "user"])
    fun deleteBySeriesAndUser(series: Series, user: User): Int

    // -----------[Question]-----------
    fun existsByQuestionAndUser(question: Question, user: User): Boolean

    @EntityGraph(attributePaths = ["question", "user"])
    fun deleteByQuestionAndUser(question: Question, user: User): Int
}