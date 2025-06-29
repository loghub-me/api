package kr.loghub.api.entity.user

import jakarta.persistence.*
import kr.loghub.api.entity.question.Question
import java.time.LocalDateTime

@Entity
@Inheritance
@Table(name = "user_posts")
data class UserPost(
    @Id
    val path: String,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    val questionStatus: Question.Status?,

    @Column(nullable = false)
    val updatedAt: LocalDateTime,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "writer_id", nullable = false)
    val user: User,
)