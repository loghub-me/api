package kr.loghub.api.repository.article

import kr.loghub.api.entity.article.ArticleComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleCommentRepository : JpaRepository<ArticleComment, Long> {
    fun findByArticleId(articleId: Long, pageable: Pageable): Page<ArticleComment>
    fun findByArticleIdAndId(articleId: Long, commentId: Long): ArticleComment?
}