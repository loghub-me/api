package kr.loghub.api.entity.article

import jakarta.persistence.*
import kr.loghub.api.entity.PublicEntity
import kr.loghub.api.entity.user.User
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "article_comments")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class ArticleComment(
    @Column(name = "content", nullable = false, length = 512)
    val content: String,

    @Column(name = "deleted", nullable = false)
    var deleted: Boolean = false,

    @Column(name = "reply_count", nullable = false)
    var replyCount: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    val article: Article,

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    val replies: MutableList<ArticleComment> = mutableListOf(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = true)
    val parent: ArticleComment?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mention_id", nullable = true)
    val mention: User?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    val writer: User,
) : PublicEntity() {
    fun delete() {
        deleted = true
    }

    fun incrementReplyCount() {
        replyCount++
    }

    fun decrementReplyCount() {
        replyCount--
    }
}