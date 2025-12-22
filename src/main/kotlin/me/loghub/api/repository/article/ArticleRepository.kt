package me.loghub.api.repository.article

import me.loghub.api.dto.common.SitemapItemProjection
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.user.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ArticleRepository : JpaRepository<Article, Long> {
    private companion object {
        const val SELECT_ARTICLE = "SELECT a FROM Article a"
        const val EXISTS_ARTICLE = "SELECT COUNT(a) > 0 FROM Article a"
        const val BY_ID = "a.id = :id"
        const val BY_COMPOSITE_KEY = "a.writerUsername = :username AND a.slug = :slug"
    }

    @Query("$SELECT_ARTICLE WHERE $BY_ID")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterById(id: Long): Article?

    @Query("$SELECT_ARTICLE WHERE $BY_COMPOSITE_KEY")
    @EntityGraph(attributePaths = ["writer"])
    fun findWithWriterByCompositeKey(username: String, slug: String): Article?

    @Query("$SELECT_ARTICLE JOIN a.topics t WHERE t.slug = :topicSlug ORDER BY a.stats.trendingScore DESC LIMIT 10")
    fun findTop10ByTopicIdOrderByTrendingScoreDesc(topicSlug: String): List<Article>

    @Query(
        value = """
        SELECT CONCAT(:clientHost, '/articles/', a.writer_username, '/', a.slug) AS url,
        to_char(a.updated_at, 'YYYY-MM-DD"T"HH24:MI:SS"+09:00"') AS lastModified,
        'weekly' AS changeFrequency,
        0.8 AS priority,
        ARRAY[CONCAT(:assetsHost, '/', a.thumbnail)] AS images
        FROM articles a
        WHERE a.published = TRUE
    """, nativeQuery = true
    )
    fun findSitemap(
        @Param("clientHost") clientHost: String,
        @Param("assetsHost") assetsHost: String,
        @Param("priority") priority: Double,
    ): List<SitemapItemProjection>

    fun existsByIdAndWriter(id: Long, writer: User): Boolean

    @Query("$EXISTS_ARTICLE WHERE $BY_COMPOSITE_KEY")
    fun existsByCompositeKey(username: String, slug: String): Boolean

    @Query("$EXISTS_ARTICLE WHERE $BY_COMPOSITE_KEY AND a.id <> :id")
    fun existsByCompositeKeyAndIdNot(username: String, slug: String, id: Long): Boolean

    @Modifying
    @Query("UPDATE Article a SET a.writerUsername = :newUsername WHERE a.writerUsername = :oldUsername")
    fun updateWriterUsernameByWriterUsername(
        @Param("oldUsername") oldUsername: String,
        @Param("newUsername") newUsername: String
    ): Int
}