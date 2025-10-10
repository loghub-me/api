package me.loghub.api.repository.topic

import me.loghub.api.entity.topic.Topic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface TopicRepository : JpaRepository<Topic, Long> {
    fun findBySlugIn(slugs: List<String>): Set<Topic>

    fun findBySlug(slug: String): Topic?

    fun findAllByOrderByTrendingScoreDesc(): List<Topic>

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