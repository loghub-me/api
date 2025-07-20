package kr.loghub.api.entity.topic

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import kr.loghub.api.entity.PublicEntity
import org.hibernate.annotations.DynamicUpdate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@Table(name = "topics")
@DynamicUpdate
@EntityListeners(AuditingEntityListener::class)
class Topic(
    @Column(name = "slug", nullable = false, length = 50)
    val slug: String,

    @Column(name = "name", nullable = false, length = 50)
    val name: String,

    @Column(nullable = false)
    val description: String,

    @Column(name = "trending_score", nullable = false)
    val trendingScore: Int = 0,
) : PublicEntity()