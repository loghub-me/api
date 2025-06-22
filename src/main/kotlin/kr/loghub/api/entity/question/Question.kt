package kr.loghub.api.entity.question

import jakarta.persistence.*
import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.question.PostQuestionDTO
import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.entity.PublicEntity
import kr.loghub.api.entity.common.RowMetadata
import kr.loghub.api.entity.topic.Topic
import kr.loghub.api.entity.user.User
import kr.loghub.api.lib.jpa.TopicsFlatConverter
import kr.loghub.api.util.checkField
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "questions")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class Question(
    @Column(name = "slug", nullable = false, length = 100)
    var slug: String,

    @Column(name = "title", nullable = false, length = 100)
    var title: String,

    @Column(name = "content", nullable = false)
    var content: String,

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType::class)
    var status: Status = Status.OPEN,

    @Column(name = "solved_at", nullable = true)
    var solvedAt: LocalDateTime? = null,

    @Column(name = "closed_at", nullable = true)
    var closedAt: LocalDateTime? = null,

    @Embedded
    var stats: QuestionStats = QuestionStats(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    val writer: User,

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "question_topics",
        joinColumns = [JoinColumn(name = "question_id")],
        inverseJoinColumns = [JoinColumn(name = "topic_id")]
    )
    var topics: MutableSet<Topic> = mutableSetOf(),

    @OneToMany(mappedBy = "question", cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @OrderBy("createdAt ASC")
    var answers: MutableList<Answer> = mutableListOf(),

    @Column(nullable = false, length = 12)
    val writerUsername: String,  // for search(denormalization)

    @Column(nullable = false)
    @Convert(converter = TopicsFlatConverter::class)
    var topicsFlat: List<TopicDTO>,  // for search(denormalization)

    @Embedded
    var rowMetadata: RowMetadata = RowMetadata(),
) : PublicEntity() {
    enum class Status { OPEN, CLOSED, SOLVED }

    fun update(requestBody: PostQuestionDTO) {
        this.title = requestBody.title
        this.content = requestBody.content
    }

    fun updateSlug(slug: String) {
        this.slug = slug
    }

    fun updateTopics(topics: List<TopicDTO>) {
        this.topics.clear()
        this.topicsFlat = topics
    }

    fun solved() {
        checkField(this::status.name, this.status == Status.OPEN) {
            ResponseMessage.Question.STATUS_MUST_BE_OPEN
        }
        this.status = Status.SOLVED
        this.solvedAt = LocalDateTime.now()
    }

    fun close() {
        checkField(this::status.name, this.status == Status.OPEN) {
            ResponseMessage.Question.STATUS_MUST_BE_OPEN
        }
        this.status = Status.CLOSED
        this.closedAt = LocalDateTime.now()
    }

    fun incrementStarCount() {
        stats.starCount++
    }
}