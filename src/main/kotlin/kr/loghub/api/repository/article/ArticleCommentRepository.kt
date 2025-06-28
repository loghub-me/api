package kr.loghub.api.repository.article

import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.article.ArticleComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleCommentRepository : JpaRepository<ArticleComment, Long> {
    @EntityGraph(attributePaths = ["writer"])
    fun findWithGraphByArticleAndParentIsNull(article: Article, pageable: Pageable): Page<ArticleComment>

    @EntityGraph(attributePaths = ["writer", "mention"])
    fun findWithGraphByArticleIdAndParentOrderByCreatedAtDesc(
        articleId: Long,
        parent: ArticleComment
    ): List<ArticleComment>

    @EntityGraph(attributePaths = ["writer", "parent", "article"])
    fun findWithGraphByArticleIdAndId(articleId: Long, commentId: Long): ArticleComment?

    fun findByArticleAndId(article: Article, id: Long): ArticleComment?
}