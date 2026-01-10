package me.loghub.api.repository.user

import me.loghub.api.dto.user.UserStatsProjection
import me.loghub.api.entity.user.UserMeta
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserMetaRepository : JpaRepository<UserMeta, Long> {
    @Query(
        value = """
        SELECT
            (
            SELECT
                (SELECT COUNT(*) FROM articles WHERE writer_id = :writerId) +
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
}
