package me.loghub.api.repository.user

import me.loghub.api.dto.user.activity.UserActivityProjection
import me.loghub.api.dto.user.activity.UserActivitySummaryDTO
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface UserActivityRepository : JpaRepository<UserActivity, Long> {
    private companion object {
        const val SELECT_SUMMARY_DTO =
            "SELECT new me.loghub.api.dto.user.activity.UserActivitySummaryDTO(ua.createdDate, COUNT(ua)) FROM UserActivity ua"
        const val BY_USER = "ua.user = :user"
        const val BY_CREATED_DATE_BETWEEN = "ua.createdDate BETWEEN :from AND :to"
        const val GROUP_BY_CREATED_DATE = "GROUP BY ua.createdDate"
        const val ORDER_BY_CREATED_DATE_ASC = "ORDER BY ua.createdDate ASC"
    }

    private object NativeQuery {
        const val SELECT_PROJECTION_BY_USER_AND_CREATED_DATE = """
            SELECT ua.id,
            CASE
                WHEN ua.action = 'POST_ARTICLE' THEN a.title
                WHEN ua.action = 'POST_SERIES' THEN s.title
                WHEN ua.action = 'POST_SERIES_CHAPTER' THEN sc.title
                WHEN ua.action = 'POST_QUESTION' THEN q.title
                WHEN ua.action = 'POST_QUESTION_ANSWER' THEN qa.title
            END AS title,
            CASE
                WHEN ua.action = 'POST_ARTICLE' THEN CONCAT('/articles/', a.writer_username, '/', a.slug)
                WHEN ua.action = 'POST_SERIES' THEN CONCAT('/series/', s.writer_username, '/', s.slug)
                WHEN ua.action = 'POST_SERIES_CHAPTER' THEN CONCAT('/series/', s.writer_username, '/', s.slug, '/', sc.sequence)
                WHEN ua.action = 'POST_QUESTION' THEN CONCAT('/questions/', q.writer_username, '/', q.slug)
                WHEN ua.action = 'POST_QUESTION_ANSWER' THEN CONCAT('/questions/', q.writer_username, '/', q.slug, '#answer-', qa.id)
            END AS href,
            ua.action
            FROM user_activities ua
            LEFT JOIN articles a ON ua.article_id = a.id
            LEFT JOIN series s ON ua.series_id = s.id
            LEFT JOIN series_chapters sc ON ua.series_chapter_id = sc.id
            LEFT JOIN questions q ON ua.question_id = q.id
            LEFT JOIN question_answers qa ON ua.question_answer_id = qa.id
            WHERE ua.user_id = :userId AND ua.created_date = :createdDate
            ORDER BY ua.id DESC;
        """
    }

    @Query("$SELECT_SUMMARY_DTO WHERE $BY_USER AND $BY_CREATED_DATE_BETWEEN $GROUP_BY_CREATED_DATE $ORDER_BY_CREATED_DATE_ASC")
    fun findSummaryDTOsByUserAndCreatedDateBetween(
        user: User,
        from: LocalDate,
        to: LocalDate,
    ): List<UserActivitySummaryDTO>

    @Query(value = NativeQuery.SELECT_PROJECTION_BY_USER_AND_CREATED_DATE, nativeQuery = true)
    fun findProjectionByUserIdAndCreatedDate(userId: Long, createdDate: LocalDate): List<UserActivityProjection>
}