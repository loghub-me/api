package me.loghub.api.repository.series

import me.loghub.api.entity.series.Series
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface SeriesStatsRepository : JpaRepository<Series, Long> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Series s SET s.stats.starCount = s.stats.starCount + 1 WHERE s.id = :id")
    fun incrementStarCount(id: Long)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE Series s
        SET s.stats.starCount = CASE WHEN s.stats.starCount > 0 THEN s.stats.starCount - 1 ELSE 0 END
        WHERE s.id = :id"""
    )
    fun decrementStarCount(id: Long)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Series s SET s.stats.reviewCount = s.stats.reviewCount + 1 WHERE s.id = :id")
    fun incrementReviewCount(id: Long)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE Series s
        SET s.stats.reviewCount = CASE WHEN s.stats.reviewCount > 0 THEN s.stats.reviewCount - 1 ELSE 0 END
        WHERE s.id = :id"""
    )
    fun decrementReviewCount(id: Long)
}