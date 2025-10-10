package me.loghub.api.entity.topic

import jakarta.persistence.*
import org.hibernate.annotations.Immutable

@Entity
@Table(name = "trending_topics")
@Immutable // view
class TrendingTopic(
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    val topic: Topic,

    @Column(name = "trending_score", nullable = false)
    val trendingScore: Int,
)