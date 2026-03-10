package me.loghub.api.entity.notification

import jakarta.persistence.*
import me.loghub.api.entity.PublicEntity
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.user.User
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType
import java.time.LocalDateTime

@Entity
@Table(name = "notifications")
@DynamicUpdate
class Notification(
    @Column(name = "read_at")
    var readAt: LocalDateTime? = null,

    @Column(name = "type", nullable = false)
    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val type: Type = Type.INFO,

    @Column(name = "target_type", nullable = false)
    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val targetType: TargetType,

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "article_id", nullable = true)
    val article: Article? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "series_id", nullable = true)
    val series: Series? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "question_id", nullable = true)
    val question: Question? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    val actor: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipient_id", nullable = false)
    val recipient: User,
) : PublicEntity() {
    val read get() = readAt != null

    enum class Type {
        INFO, SUCCESS, WARNING, ERROR
    }

    enum class TargetType(val targetName: String) {
        ARTICLE("아티클"), ARTICLE_COMMENT("댓글"),
        SERIES("시리즈"), SERIES_REVIEW("리뷰"),
        QUESTION("질문"), QUESTION_ANSWER("답변"),
    }

    fun markAsRead() {
        if (!read) {
            readAt = LocalDateTime.now()
        }
    }
}