package me.loghub.api.entity.question

import jakarta.persistence.*
import me.loghub.api.dto.question.answer.PostQuestionAnswerDTO
import me.loghub.api.entity.PublicEntity
import me.loghub.api.entity.user.User
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "question_answers")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class QuestionAnswer(
    @Column(name = "title", nullable = false, length = 56)
    var title: String,

    @Column(name = "content", nullable = false, length = 16384)
    var content: String,

    @Column(name = "accepted", nullable = false)
    var accepted: Boolean = false,

    @Column(name = "accepted_at", nullable = false)
    var acceptedAt: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    val question: Question,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", nullable = false)
    val writer: User,
) : PublicEntity() {
    fun update(requestBody: PostQuestionAnswerDTO) {
        this.title = requestBody.title
        this.content = requestBody.content
    }

    fun accept() {
        this.accepted = true
        this.acceptedAt = LocalDateTime.now()
    }
}