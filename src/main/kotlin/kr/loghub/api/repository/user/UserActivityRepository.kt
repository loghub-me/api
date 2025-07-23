package kr.loghub.api.repository.user

import kr.loghub.api.dto.user.activity.UserActivityDTO
import kr.loghub.api.dto.user.activity.UserActivityDetailDTO
import kr.loghub.api.entity.user.UserActivity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface UserActivityRepository : JpaRepository<UserActivity, Long> {
    private companion object {
        const val SELECT_DTO =
            "SELECT new kr.loghub.api.dto.user.activity.UserActivityDTO(ua.createdDate, COUNT(ua)) FROM UserActivity ua"
        const val SELECT_DETAIL_DTO = """
        SELECT new kr.loghub.api.dto.user.activity.UserActivityDetailDTO(
            ua.id,
            CASE
                WHEN ua.action = 'JOIN' THEN NULL
                WHEN ua.action = 'POST_ARTICLE' THEN ua.article.title
                WHEN ua.action = 'POST_SERIES' THEN ua.series.title
                WHEN ua.action = 'POST_QUESTION' THEN ua.question.title
            END,
            ua.createdDate,
            ua.action,
            ua.user.id
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

    @Query("$SELECT_DTO WHERE $BY_USER_ID AND $BY_CREATED_DATE_BETWEEN $GROUP_BY_CREATED_DATE $ORDER_BY_CREATED_DATE_ASC")
    fun findDTOsByCreatedDateBetween(
        userId: Long,
        from: LocalDate,
        to: LocalDate,
    ): List<UserActivityDTO>

    @Query("$SELECT_DETAIL_DTO WHERE $BY_USER_ID AND $BY_CREATED_DATE $ORDER_BY_CREATED_AT_DESC")
    fun findDetailDTOByUserIdAndCreatedDate(userId: Long, createdDate: LocalDate): List<UserActivityDetailDTO>
}