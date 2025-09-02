package me.loghub.api.repository.article

import me.loghub.api.entity.article.Article
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

    @Query("$EXISTS_ARTICLE WHERE $BY_COMPOSITE_KEY")
    fun existsByCompositeKey(username: String, slug: String): Boolean

    @Modifying
    @Query("UPDATE Article a SET a.stats.starCount = a.stats.starCount - 1 WHERE $BY_ID")
    fun decrementStarCount(id: Long)

    @Modifying
    @Query("UPDATE Article a SET a.stats.trendingScore = :trendingScore WHERE $BY_ID")
    fun updateTrendingScoreById(trendingScore: Double, id: Long): Int

    @Modifying
    @Query("UPDATE Article a SET a.stats.trendingScore = 0")
    fun clearTrendingScore(): Int

    @Modifying
    @Query("UPDATE Article a SET a.writerUsername = :newUsername WHERE a.writerUsername = :oldUsername")
    fun updateWriterUsernameByWriterUsername(
        @Param("oldUsername") oldUsername: String,
        @Param("newUsername") newUsername: String
    ): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Article a SET a.stats.commentCount = a.stats.commentCount + 1 WHERE a.id = :id")
    fun incrementCommentCount(id: Long)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE Article a
        SET a.stats.commentCount = CASE WHEN a.stats.commentCount > 0 THEN a.stats.commentCount - 1 ELSE 0 END
        WHERE a.id = :id"""
    )
    fun decrementCommentCount(id: Long)
}