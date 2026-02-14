package me.loghub.api.repository.article

import me.loghub.api.entity.article.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ArticleStatsRepository : JpaRepository<Article, Long> {
    @Modifying
    @Query("UPDATE Article a SET a.stats.starCount = a.stats.starCount + 1 WHERE a.id = :id")
    fun incrementStarCount(id: Long)

    @Modifying
    @Query(
        """
        UPDATE Article a
        SET a.stats.starCount = CASE WHEN a.stats.starCount > 0 THEN a.stats.starCount - 1 ELSE 0 END
        WHERE a.id = :id
        """
    )
    fun decrementStarCount(id: Long)

    @Modifying
    @Query("UPDATE Article a SET a.stats.commentCount = a.stats.commentCount + 1 WHERE a.id = :id")
    fun incrementCommentCount(id: Long)

    @Modifying
    @Query(
        """
        UPDATE Article a
        SET a.stats.commentCount = CASE WHEN a.stats.commentCount > 0 THEN a.stats.commentCount - 1 ELSE 0 END
        WHERE a.id = :id
        """
    )
    fun decrementCommentCount(id: Long)
}