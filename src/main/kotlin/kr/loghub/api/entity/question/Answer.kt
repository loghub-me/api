package kr.loghub.api.entity.question

import jakarta.persistence.*
import kr.loghub.api.dto.question.answer.PostAnswerDTO
import kr.loghub.api.entity.PublicEntity
import kr.loghub.api.entity.user.User
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "answers")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class Answer(
    @Column(name = "content", nullable = false)
    var content: String,

    @Column(name = "accepted", nullable = false)
    var accepted: Boolean = false,

    @Column(name = "accepted_at", nullable = false, updatable = false)
    var acceptedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    val question: Question,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    val writer: User,
) : PublicEntity() {
    fun update(requestBody: PostAnswerDTO) {
        this.content = requestBody.content
    }

    fun accept() {
        this.accepted = true
        this.acceptedAt = LocalDateTime.now()
    }
}