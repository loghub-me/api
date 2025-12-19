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
                COALESCE(us.article.id, us.series.id, us.question.id),
                COALESCE(us.article.slug, us.series.slug, us.question.slug),
                COALESCE(us.article.title, us.series.title, us.question.title),
                COALESCE(us.article.stats.starCount, us.series.stats.starCount, us.question.stats.starCount),
                new me.loghub.api.dto.user.UserDTO(
                    COALESCE(us.article.writer.id, us.series.writer.id, us.question.writer.id),
                    COALESCE(us.article.writerUsername, us.series.writerUsername, us.question.writerUsername)
                ),
                COALESCE(us.article.topicsFlat, us.series.topicsFlat, us.question.topicsFlat),
                us.target
            )
            FROM UserStar us
            LEFT JOIN us.article
            LEFT JOIN us.series
            LEFT JOIN us.question
        """
        const val BY_USER = "us.user._username = :username"
        const val CREATED_AT_DESC = "us.createdAt DESC"
    }

    @Query("$SELECT_DTO WHERE $BY_USER ORDER BY $CREATED_AT_DESC")
    fun findDTOsByUsername(username: String, pageable: Pageable): Page<UserStarDTO>

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