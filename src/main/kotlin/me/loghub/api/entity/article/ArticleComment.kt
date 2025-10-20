package me.loghub.api.entity.article

import jakarta.persistence.*
import me.loghub.api.dto.article.comment.PostArticleCommentDTO
import me.loghub.api.entity.PublicEntity
import me.loghub.api.entity.user.User
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "article_comments")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class ArticleComment(
    @Column(name = "content", nullable = false, length = 512)
    var content: String,

    @Column(name = "deleted", nullable = false)
    var deleted: Boolean = false,

    @Column(name = "reply_count", nullable = false)
    var replyCount: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    val article: Article,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_id", nullable = true)
    val parent: ArticleComment?,

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "mention_id", nullable = true)
    val mention: User?,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", nullable = false)
    val writer: User,
) : PublicEntity() {
    fun update(requestBody: PostArticleCommentDTO) {
        this.content = requestBody.content
    }

    fun delete() {
        deleted = true
    }
}