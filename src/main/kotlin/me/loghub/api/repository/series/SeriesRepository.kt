package me.loghub.api.repository.series

import me.loghub.api.dto.common.SitemapItemProjection
import me.loghub.api.entity.series.Series
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SeriesRepository : JpaRepository<Series, Long> {
    private companion object {
        const val SELECT_SERIES = "SELECT s FROM Series s"
        const val EXISTS_SERIES = "SELECT COUNT(s) > 0 FROM Series s"
        const val BY_ID = "s.id = :id"
        const val BY_COMPOSITE_KEY = "s.writerUsername = :username AND s.slug = :slug"
    }

    @Query("$SELECT_SERIES WHERE $BY_ID")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterById(id: Long): Series?

    @Query("$SELECT_SERIES WHERE $BY_ID")
    @EntityGraph(attributePaths = ["writer", "chapters"])
    fun findWithGraphById(id: Long): Series?

    @Query("$SELECT_SERIES WHERE $BY_COMPOSITE_KEY")
    @EntityGraph(attributePaths = ["writer", "chapters"])
    fun findWithGraphByCompositeKey(username: String, slug: String): Series?

    @Query("$SELECT_SERIES JOIN s.topics t WHERE t.slug = :topicSlug ORDER BY s.stats.trendingScore DESC LIMIT 10")
    fun findTop10ByTopicIdOrderByTrendingScoreDesc(topicSlug: String): List<Series>

    @Query(
        value = """
        SELECT CONCAT(:clientHost, '/series/', s.writer_username, '/', s.slug) AS url,
        to_char(s.updated_at, 'YYYY-MM-DD"T"HH24:MI:SS"+09:00"') AS lastModified,
        'weekly' AS changeFrequency,
        :seriesPriority AS priority,
        ARRAY[CONCAT(:assetsHost, '/', s.thumbnail)] AS images
        FROM series s
        UNION ALL
        SELECT CONCAT(:clientHost, '/series/', s.writer_username, '/', s.slug, '/', sc.sequence) AS url,
        to_char(sc.updated_at, 'YYYY-MM-DD"T"HH24:MI:SS"+09:00"') AS lastModified,
        'weekly' AS changeFrequency,
        :chapterPriority AS priority,
        ARRAY[CONCAT(:assetsHost, '/', s.thumbnail)] AS images
        FROM series s
        JOIN series_chapters sc ON sc.series_id = s.id
        WHERE sc.published = TRUE
    """, nativeQuery = true
    )
    fun findSitemap(
        @Param("clientHost") clientHost: String,
        @Param("assetsHost") assetsHost: String,
        @Param("seriesPriority") seriesPriority: Double,
        @Param("chapterPriority") chapterPriority: Double,
    ): List<SitemapItemProjection>

    @Query("$EXISTS_SERIES WHERE $BY_COMPOSITE_KEY")
    fun existsByCompositeKey(username: String, slug: String): Boolean

    @Query("$EXISTS_SERIES WHERE $BY_COMPOSITE_KEY AND s.id <> :id")
    fun existsByCompositeKeyAndIdNot(username: String, slug: String, id: Long): Boolean

    @Modifying
    @Query("UPDATE Series s SET s.writerUsername = :newUsername WHERE s.writerUsername = :oldUsername")
    fun updateWriterUsernameByWriterUsername(
        @Param("oldUsername") oldUsername: String,
        @Param("newUsername") newUsername: String
    ): Int
}