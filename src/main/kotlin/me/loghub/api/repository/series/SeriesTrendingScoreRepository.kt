package me.loghub.api.repository.series

import me.loghub.api.entity.series.Series
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SeriesTrendingScoreRepository : JpaRepository<Series, Long> {
    @Modifying
    @Query("UPDATE Series s SET s.stats.trendingScore = s.stats.trendingScore + :trendingScore WHERE s.id = :id")
    fun incrementTrendingScoreById(@Param("trendingScore") trendingScore: Double, @Param("id") id: Long): Int

    @Modifying
    @Query("UPDATE Series s SET s.stats.trendingScore = s.stats.trendingScore * :factor")
    fun decayTrendingScores(@Param("factor") factor: Double): Int

    @Modifying
    @Query("UPDATE Series s SET s.stats.trendingScore = 0 WHERE s.stats.trendingScore < :threshold")
    fun clearLowTrendingScores(@Param("threshold") threshold: Double): Int
}