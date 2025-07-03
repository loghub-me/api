package kr.loghub.api.repository.article

import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.article.ArticleComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ArticleCommentRepository : JpaRepository<ArticleComment, Long> {
    companion object {
        const val SELECT_COMMENT = "SELECT ac FROM ArticleComment ac"
        const val BY_ARTICLE_ID = "ac.article.id = :articleId"
        const val BY_PARENT_ID = "ac.parent.id = :parentId"
        const val BY_PARENT_IS_NULL = "ac.parent IS NULL"
    }

    @EntityGraph(attributePaths = ["writer"])
    @Query("$SELECT_COMMENT WHERE $BY_ARTICLE_ID AND $BY_PARENT_IS_NULL")
    fun findRootsByArticleId(articleId: Long, pageable: Pageable): Page<ArticleComment>

    @EntityGraph(attributePaths = ["writer", "mention"])
    @Query("$SELECT_COMMENT WHERE $BY_ARTICLE_ID AND $BY_PARENT_ID")
    fun findAllByArticleIdAndParentId(articleId: Long, parentId: Long): List<ArticleComment>

    @EntityGraph(attributePaths = ["writer", "parent", "article"])
    fun findWithGraphByArticleIdAndId(articleId: Long, commentId: Long): ArticleComment?

    fun findByArticleAndId(article: Article, id: Long): ArticleComment?
}