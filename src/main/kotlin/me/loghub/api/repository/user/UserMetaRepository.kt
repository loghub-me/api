package me.loghub.api.repository.user

import me.loghub.api.dto.topic.TopicUsageProjection
import me.loghub.api.dto.user.UserStatsProjection
import me.loghub.api.entity.user.UserMeta
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserMetaRepository : JpaRepository<UserMeta, Long> {
    @Query(
        value = """
        SELECT
            (
            SELECT
                (SELECT COUNT(*) FROM articles WHERE writer_id = :writerId AND published = true) +
                (SELECT COUNT(*) FROM series WHERE writer_id = :writerId) +
                (SELECT COUNT(*) FROM questions WHERE writer_id = :writerId)
            ) as total_posted_count,
            (
            SELECT COUNT(*) FROM user_stars WHERE stargazer_id = :writerId
            )
            as total_added_star_count,
            (
            (SELECT COUNT(*) FROM user_stars us JOIN articles a ON us.article_id = a.id WHERE a.writer_id = :writerId) +
            (SELECT COUNT(*) FROM user_stars us JOIN series s ON us.series_id = s.id WHERE s.writer_id = :writerId) +
            (SELECT COUNT(*) FROM user_stars us JOIN questions q ON us.question_id = q.id WHERE q.writer_id = :writerId)
            ) total_gazed_star_count
        """,
        nativeQuery = true,
    )
    fun countStatsByWriterId(writerId: Long): UserStatsProjection

    @Query(
        value = """
        SELECT t.slug, t.name, COUNT(*) AS count FROM (
        SELECT at.topic_id AS topic_id
        FROM articles a
        LEFT JOIN public.article_topics at ON a.id = at.article_id
        WHERE a.writer_id = :writerId
        UNION ALL
        SELECT st.topic_id AS topic_id
        FROM series s
        LEFT JOIN public.series_topics st ON s.id = st.series_id
        WHERE s.writer_id = :writerId
        UNION ALL
        SELECT at.topic_id AS topic_id
        FROM questions a
        LEFT JOIN public.question_topics at ON a.id = at.question_id
        WHERE a.writer_id = :writerId
        ) AS combined
        LEFT JOIN topics t ON combined.topic_id = t.id
        GROUP BY t.id
        ORDER BY count DESC
        LIMIT 5
        """, nativeQuery = true
    )
    fun findTopicUsageTop5ByWriter(@Param("writerId") writerId: Long): List<TopicUsageProjection>
}
