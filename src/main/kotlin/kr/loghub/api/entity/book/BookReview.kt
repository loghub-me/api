package kr.loghub.api.entity.book

import jakarta.persistence.*
import kr.loghub.api.entity.PublicEntity
import kr.loghub.api.entity.user.User
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "book_reviews")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class BookReview(
    @Column(name = "content", nullable = false, length = 512)
    val content: String,

    @Column(name = "rating", nullable = false)
    val rating: Int,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    val book: Book,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", nullable = false)
    val writer: User,
) : PublicEntity()