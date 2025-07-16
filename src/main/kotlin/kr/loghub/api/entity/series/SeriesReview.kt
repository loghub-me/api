package kr.loghub.api.entity.series

import jakarta.persistence.*
import kr.loghub.api.entity.PublicEntity
import kr.loghub.api.entity.user.User
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "series_reviews")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class SeriesReview(
    @Column(name = "content", nullable = false, length = 512)
    val content: String,

    @Column(name = "rating", nullable = false)
    val rating: Int,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "series_id", nullable = false)
    val series: Series,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "writer_id", nullable = false)
    val writer: User,
) : PublicEntity()