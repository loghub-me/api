package me.loghub.api.entity.question

import jakarta.persistence.*
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.question.PostQuestionDTO
import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.entity.PublicEntity
import me.loghub.api.entity.topic.Topic
import me.loghub.api.entity.user.User
import me.loghub.api.lib.jpa.TopicsFlatConverter
import me.loghub.api.util.checkField
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.Formula
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "questions")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class Question(
    @Column(name = "slug", nullable = false, length = 128)
    var slug: String,

    @Column(name = "title", nullable = false, length = 56)
    var title: String,

    @Column(name = "content", nullable = false, length = 16384)
    var content: String,

    @Column(name = "normalized_content", nullable = false, length = 16384)
    var normalizedContent: String,

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType::class)
    var status: Status = Status.OPEN,

    @Column(name = "solved_at", nullable = true)
    var solvedAt: LocalDateTime? = null,

    @Column(name = "closed_at", nullable = true)
    var closedAt: LocalDateTime? = null,

    @Embedded
    var stats: QuestionStats = QuestionStats(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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
    var answers: MutableList<QuestionAnswer> = mutableListOf(),

    @Column(nullable = false, length = 12)
    val writerUsername: String,  // for search(denormalization)

    @Column(nullable = false)
    @Convert(converter = TopicsFlatConverter::class)
    var topicsFlat: List<TopicDTO>,  // for search(denormalization)

    @Formula("tableoid")
    val tableoid: String? = null,

    @Formula("ctid")
    val ctid: String? = null,
) : PublicEntity() {
    enum class Status { OPEN, CLOSED, SOLVED }

    fun update(requestBody: PostQuestionDTO, slug: String, normalizedContent: String) {
        this.title = requestBody.title
        this.content = requestBody.content
        this.slug = slug
        this.normalizedContent = normalizedContent
    }

    fun updateTopics(topics: Set<Topic>) {
        this.topics.clear()
        this.topics = topics.toMutableSet()
        this.topicsFlat = TopicsFlatConverter.toFlat(topics)
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
}