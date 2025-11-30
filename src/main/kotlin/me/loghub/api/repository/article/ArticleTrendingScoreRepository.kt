package me.loghub.api.repository.article

import me.loghub.api.entity.article.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ArticleTrendingScoreRepository : JpaRepository<Article, Long> {
    @Modifying
    @Query("UPDATE Article a SET a.stats.trendingScore = a.stats.trendingScore + :trendingScore WHERE a.id = :id")
    fun incrementTrendingScoreById(@Param("trendingScore") trendingScore: Double, @Param("id") id: Long): Int

    @Modifying
    @Query("UPDATE Article a SET a.stats.trendingScore = a.stats.trendingScore * :factor")
    fun decayTrendingScores(@Param("factor") factor: Double): Int

    @Modifying
    @Query("UPDATE Article a SET a.stats.trendingScore = 0 WHERE a.stats.trendingScore < :threshold")
    fun clearLowTrendingScores(@Param("threshold") threshold: Double): Int
}