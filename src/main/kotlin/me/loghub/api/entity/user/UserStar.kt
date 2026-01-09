package me.loghub.api.entity.user

import jakarta.persistence.*
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.series.Series
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "user_stars")
class UserStar(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "target", nullable = false)
    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val target: Target,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

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
    @JoinColumn(name = "stargazer_id", nullable = false)
    val stargazer: User,
) {
    enum class Target { ARTICLE, SERIES, QUESTION }
}