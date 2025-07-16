package kr.loghub.api.repository.series

import kr.loghub.api.entity.series.Series
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SeriesRepository : JpaRepository<Series, Long> {
    companion object {
        const val SELECT_SERIES = "SELECT b FROM Series b"
        const val EXISTS_SERIES = "SELECT COUNT(b) > 0 FROM Series b"
        const val BY_ID = "b.id = :id"
        const val BY_COMPOSITE_KEY = "b.writerUsername = :username AND b.slug = :slug"
    }

    @Query("$SELECT_SERIES WHERE $BY_ID")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterById(id: Long): Series?

    @Query("$SELECT_SERIES WHERE $BY_COMPOSITE_KEY")
    @EntityGraph(attributePaths = ["writer", "chapters"])
    fun findWithGraphByCompositeKey(username: String, slug: String): Series?

    @Query("$EXISTS_SERIES WHERE $BY_COMPOSITE_KEY")
    fun existsByCompositeKey(username: String, slug: String): Boolean

    @Modifying
    @Query("UPDATE Series a SET a.stats.starCount = a.stats.starCount - 1 WHERE a.id = :id")
    fun decrementStarCount(id: Long)

    @Modifying
    @Query("UPDATE Series a SET a.stats.trendingScore = :trendingScore WHERE a.id = :id")
    fun updateTrendingScoreById(trendingScore: Double, id: Long): Int

    @Modifying
    @Query("UPDATE Series a SET a.stats.trendingScore = 0")
    fun clearTrendingScore(): Int

    @Modifying
    @Query("UPDATE Series a SET a.writerUsername = :newUsername WHERE a.writerUsername = :oldUsername")
    fun updateWriterUsernameByWriterUsername(
        @Param("oldUsername") oldUsername: String,
        @Param("newUsername") newUsername: String
    ): Int
}