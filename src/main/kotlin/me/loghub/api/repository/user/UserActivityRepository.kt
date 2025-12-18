package me.loghub.api.repository.user

import me.loghub.api.dto.user.activity.UserActivityProjection
import me.loghub.api.dto.user.activity.UserActivitySummaryDTO
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.series.SeriesChapter
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
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
                WHEN ua.action = 'PUBLISH_ARTICLE' THEN a.title
                WHEN ua.action = 'POST_SERIES' THEN s.title
                WHEN ua.action = 'PUBLISH_SERIES_CHAPTER' THEN sc.title
                WHEN ua.action = 'POST_QUESTION' THEN q.title
                WHEN ua.action = 'POST_QUESTION_ANSWER' THEN qa.title
            END AS title,
            CASE
                WHEN ua.action = 'PUBLISH_ARTICLE' THEN CONCAT('/articles/', a.writer_username, '/', a.slug)
                WHEN ua.action = 'POST_SERIES' THEN CONCAT('/series/', s.writer_username, '/', s.slug)
                WHEN ua.action = 'PUBLISH_SERIES_CHAPTER' THEN CONCAT('/series/', s.writer_username, '/', s.slug, '/', sc.sequence)
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

    @Modifying
    @Query(
        value = """
        INSERT INTO user_activities (action, created_at, created_date, user_id, article_id)
        VALUES (CAST(:#{#a.action.name()} AS user_action_enum), :#{#a.createdAt}, :#{#a.createdDate}, :#{#a.user.id}, :#{#a.article.id})
        ON CONFLICT (user_id, article_id) WHERE action = 'PUBLISH_ARTICLE' DO NOTHING
    """, nativeQuery = true
    )
    fun saveArticleActivity(@Param("a") a: UserActivity)

    @Modifying
    @Query(
        value = """
        INSERT INTO user_activities (action, created_at, created_date, user_id, series_id)
        VALUES (CAST(:#{#a.action.name()} AS user_action_enum), :#{#a.createdAt}, :#{#a.createdDate}, :#{#a.user.id}, :#{#a.series.id})
        ON CONFLICT (user_id, series_id) WHERE action = 'POST_SERIES' DO NOTHING
    """, nativeQuery = true
    )
    fun saveSeriesActivity(@Param("a") a: UserActivity)

    @Modifying
    @Query(
        value = """
        INSERT INTO user_activities (action, created_at, created_date, user_id, series_id, series_chapter_id)
        VALUES (CAST(:#{#a.action.name()} AS user_action_enum), :#{#a.createdAt}, :#{#a.createdDate}, :#{#a.user.id}, :#{#a.series.id}, :#{#a.seriesChapter.id})
        ON CONFLICT (user_id, series_chapter_id) WHERE action = 'PUBLISH_SERIES_CHAPTER' DO NOTHING
    """, nativeQuery = true
    )
    fun saveSeriesChapterActivity(@Param("a") a: UserActivity)

    @Modifying
    @Query(
        value = """
        INSERT INTO user_activities (action, created_at, created_date, user_id, question_id)
        VALUES (CAST(:#{#a.action.name()} AS user_action_enum), :#{#a.createdAt}, :#{#a.createdDate}, :#{#a.user.id}, :#{#a.question.id})
        ON CONFLICT (user_id, question_id) WHERE action = 'POST_QUESTION' DO NOTHING
    """, nativeQuery = true
    )
    fun saveQuestionActivity(@Param("a") a: UserActivity)

    @Modifying
    @Query(
        value = """
        INSERT INTO user_activities (action, created_at, created_date, user_id, question_id, question_answer_id)
        VALUES (CAST(:#{#a.action.name()} AS user_action_enum), :#{#a.createdAt}, :#{#a.createdDate}, :#{#a.user.id}, :#{#a.question.id}, :#{#a.questionAnswer.id})
        ON CONFLICT (user_id, question_answer_id) WHERE action = 'POST_QUESTION_ANSWER' DO NOTHING
    """, nativeQuery = true
    )
    fun saveQuestionAnswerActivity(@Param("a") a: UserActivity)

    fun deleteByArticle(article: Article)

    fun deleteBySeriesChapter(seriesChapter: SeriesChapter)
}

fun UserActivityRepository.saveActivityIgnoreConflict(activity: UserActivity) {
    when (activity.action) {
        UserActivity.Action.PUBLISH_ARTICLE -> saveArticleActivity(activity)
        UserActivity.Action.POST_SERIES -> saveSeriesActivity(activity)
        UserActivity.Action.PUBLISH_SERIES_CHAPTER -> saveSeriesChapterActivity(activity)
        UserActivity.Action.POST_QUESTION -> saveQuestionActivity(activity)
        UserActivity.Action.POST_QUESTION_ANSWER -> saveQuestionAnswerActivity(activity)
    }
}
