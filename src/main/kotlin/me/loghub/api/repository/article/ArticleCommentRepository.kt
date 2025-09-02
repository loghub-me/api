package me.loghub.api.repository.article

import me.loghub.api.entity.article.Article
import me.loghub.api.entity.article.ArticleComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ArticleCommentRepository : JpaRepository<ArticleComment, Long> {
    private companion object {
        const val SELECT_COMMENT = "SELECT ac FROM ArticleComment ac"
        const val BY_ARTICLE = "ac.article = :article"
        const val BY_PARENT = "ac.parent = :parent"
        const val BY_PARENT_IS_NULL = "ac.parent IS NULL"
        const val CREATED_AT_DESC = "ac.createdAt DESC"
    }

    @EntityGraph(attributePaths = ["writer"])
    @Query("$SELECT_COMMENT WHERE $BY_ARTICLE AND $BY_PARENT_IS_NULL ORDER BY $CREATED_AT_DESC")
    fun findRootComments(article: Article, pageable: Pageable): Page<ArticleComment>

    @EntityGraph(attributePaths = ["writer"])
    @Query("$SELECT_COMMENT WHERE $BY_ARTICLE AND $BY_PARENT ORDER BY $CREATED_AT_DESC")
    fun findLeafComments(article: Article, parent: ArticleComment): List<ArticleComment>

    @EntityGraph(attributePaths = ["parent", "writer"])
    fun findWithGraphByArticleAndId(article: Article, commentId: Long): ArticleComment?

    fun findByArticleAndId(article: Article, id: Long): ArticleComment?

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE ArticleComment ac SET ac.replyCount = ac.replyCount + 1 WHERE ac.id = :id")
    fun incrementReplyCount(id: Long)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        UPDATE ArticleComment ac
        SET ac.replyCount = CASE WHEN ac.replyCount > 0 then ac.replyCount - 1 else 0 END
        WHERE ac.id = :id"""
    )
    fun decrementReplyCount(id: Long)
}