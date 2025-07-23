package kr.loghub.api.repository.user

import kr.loghub.api.dto.user.activity.UserActivityDTO
import kr.loghub.api.dto.user.activity.UserActivitySummaryDTO
import kr.loghub.api.entity.user.UserActivity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface UserActivityRepository : JpaRepository<UserActivity, Long> {
    private companion object {
        const val SELECT_SUMMARY_DTO =
            "SELECT new kr.loghub.api.dto.user.activity.UserActivitySummaryDTO(ua.createdDate, COUNT(ua)) FROM UserActivity ua"
        const val SELECT_DTO = """
        SELECT new kr.loghub.api.dto.user.activity.UserActivityDTO(
            ua.id,
            CASE
                WHEN ua.action = 'POST_ARTICLE' THEN ua.article.slug
                WHEN ua.action = 'POST_SERIES' THEN ua.series.slug
                WHEN ua.action = 'POST_QUESTION' THEN ua.question.slug
            END,
            CASE
                WHEN ua.action = 'POST_ARTICLE' THEN ua.article.title
                WHEN ua.action = 'POST_SERIES' THEN ua.series.title
                WHEN ua.action = 'POST_QUESTION' THEN ua.question.title
            END,
            ua.action
        )
        FROM UserActivity ua
        LEFT JOIN ua.article
        LEFT JOIN ua.series
        LEFT JOIN ua.question
        """
        const val BY_USER_ID = "ua.user.id = :userId"
        const val BY_CREATED_DATE_BETWEEN = "ua.createdDate BETWEEN :from AND :to"
        const val BY_CREATED_DATE = "ua.createdDate = :createdDate"
        const val GROUP_BY_CREATED_DATE = "GROUP BY ua.createdDate"
        const val ORDER_BY_CREATED_DATE_ASC = "ORDER BY ua.createdDate ASC"
        const val ORDER_BY_CREATED_AT_DESC = "ORDER BY ua.createdAt DESC"
    }

    @Query("$SELECT_SUMMARY_DTO WHERE $BY_USER_ID AND $BY_CREATED_DATE_BETWEEN $GROUP_BY_CREATED_DATE $ORDER_BY_CREATED_DATE_ASC")
    fun findSummaryDTOsByCreatedDateBetween(
        userId: Long,
        from: LocalDate,
        to: LocalDate,
    ): List<UserActivitySummaryDTO>

    @Query("$SELECT_DTO WHERE $BY_USER_ID AND $BY_CREATED_DATE $ORDER_BY_CREATED_AT_DESC")
    fun findDTOByUserIdAndCreatedDate(userId: Long, createdDate: LocalDate): List<UserActivityDTO>
}