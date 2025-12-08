package me.loghub.api.entity.user

import jakarta.persistence.*
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.series.SeriesChapter
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
    @JoinColumn(name = "series_chapter_id", nullable = true)
    val seriesChapter: SeriesChapter? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "question_id", nullable = true)
    val question: Question? = null,

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "question_answer_id", nullable = true)
    val questionAnswer: QuestionAnswer? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) {
    enum class Action {
        POST_ARTICLE,
        POST_SERIES,
        POST_SERIES_CHAPTER,
        POST_QUESTION,
        POST_QUESTION_ANSWER,
    }
}