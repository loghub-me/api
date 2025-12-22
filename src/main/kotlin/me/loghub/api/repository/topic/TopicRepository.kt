package me.loghub.api.repository.topic

import me.loghub.api.dto.common.SitemapItemProjection
import me.loghub.api.entity.topic.Topic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TopicRepository : JpaRepository<Topic, Long> {
    fun findBySlugIn(slugs: List<String>): Set<Topic>

    fun findBySlug(slug: String): Topic?

    fun findAllByOrderByTrendingScoreDesc(): List<Topic>

    @Query(
        value = """
        SELECT CONCAT(:clientHost, '/topics/', t.slug) AS url,
        to_char(t.updated_at, 'YYYY-MM-DD"T"HH24:MI:SS"+09:00"') AS lastModified,
        'weekly' AS changeFrequency,
        :priority AS priority
        FROM topics t
    """, nativeQuery = true
    )
    fun findSitemap(
        @Param("clientHost") clientHost: String,
        @Param("assetsHost") assetsHost: String,
        @Param("priority") priority: Double,
    ): List<SitemapItemProjection>

    @Modifying
    @Query(
        """
            UPDATE Topic t
            SET t.trendingScore = (SELECT tt.trendingScore FROM TrendingTopic tt WHERE tt.topic.id = t.id)
            WHERE t.id IN (SELECT tt.topic.id FROM TrendingTopic tt)
            """
    )
    fun updateTrendingScoresFromTrendingTopic(): Int

    @Modifying
    @Query(
        """
        UPDATE Topic t SET t.trendingScore = 0
        WHERE t.id NOT IN (SELECT tt.topic.id FROM TrendingTopic tt)
        """
    )
    fun clearTrendingScoresNotInTrendingTopic(): Int
}