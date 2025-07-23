package kr.loghub.api.entity.user

import jakarta.persistence.*
import kr.loghub.api.entity.article.Article
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.series.Series
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "user_activities")
class UserActivity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "action", nullable = false)
    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val action: Action,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "createdDate", nullable = false, updatable = false)
    val createdDate: LocalDate = createdAt.toLocalDate(),

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
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) {
    enum class Action {
        POST_ARTICLE,
        POST_SERIES,
        POST_QUESTION
    }
}