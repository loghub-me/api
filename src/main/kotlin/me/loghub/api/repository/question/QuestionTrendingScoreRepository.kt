package me.loghub.api.repository.question

import me.loghub.api.entity.question.Question
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface QuestionTrendingScoreRepository : JpaRepository<Question, Long> {
    @Modifying
    @Query("UPDATE Question q SET q.stats.trendingScore = q.stats.trendingScore + :trendingScore WHERE q.id = :id")
    fun incrementTrendingScoreById(@Param("trendingScore") trendingScore: Double, @Param("id") id: Long): Int

    @Modifying
    @Query("UPDATE Question q SET q.stats.trendingScore = q.stats.trendingScore * :factor")
    fun decayTrendingScores(@Param("factor") factor: Double): Int

    @Modifying
    @Query("UPDATE Question q SET q.stats.trendingScore = 0 WHERE q.stats.trendingScore < :threshold")
    fun clearLowTrendingScores(@Param("threshold") threshold: Double): Int
}