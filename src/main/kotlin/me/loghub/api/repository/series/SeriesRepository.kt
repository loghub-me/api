package me.loghub.api.repository.series

import me.loghub.api.dto.common.SitemapItemProjection
import me.loghub.api.dto.user.post.UserPostProjection
import me.loghub.api.entity.series.Series
import org.springframework.data.jpa.repository.*
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

    @Query(
        value = """
        SELECT sc.title,
            CONCAT(:clientHost, '/series/', s.writer_username, '/', s.slug, '/', sc.sequence) AS link,
            to_char(sc.published_at, 'YYYY-MM-DD"T"HH24:MI:SS"+09:00"') AS publishedAt
        FROM series s
        JOIN series_chapters sc ON sc.series_id = s.id
        WHERE s.writer_username = :username
          AND sc.published = TRUE
        ORDER BY sc.published_at DESC
        LIMIT :limit
    """, nativeQuery = true
    )
    fun findRecentChapterPost(
        @Param("username") username: String,
        @Param("clientHost") clientHost: String,
        @Param("limit") limit: Int,
    ): List<UserPostProjection>

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

    @NativeQuery(
        """
        UPDATE series
        SET chapter_count = chapter_count + 1
        WHERE id = :id AND chapter_count < :maxChapterSize
        RETURNING chapter_count
    """
    )
    fun increaseAndGetChapterCount(@Param("id") id: Long, @Param("maxChapterSize") maxChapterSize: Int): Int

    @Modifying
    @Query("UPDATE Series s SET s.writerUsername = :newUsername WHERE s.writerUsername = :oldUsername")
    fun updateWriterUsernameByWriterUsername(
        @Param("oldUsername") oldUsername: String,
        @Param("newUsername") newUsername: String
    ): Int
}