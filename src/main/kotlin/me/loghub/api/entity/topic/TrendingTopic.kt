package me.loghub.api.entity.topic

import jakarta.persistence.*
import org.hibernate.annotations.Immutable

@Entity
@Table(name = "trending_topics")
@Immutable // view
class TrendingTopic(
    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    val topic: Topic,

    @Column(name = "trending_score", nullable = false)
    val trendingScore: Double,
) {
    override fun equals(other: Any?) = when {
        this === other -> true
        other !is TrendingTopic -> false
        this.javaClass != other.javaClass -> false
        topic.id == null || other.topic.id == null -> false
        else -> this.topic.id == other.topic.id
    }

    override fun hashCode() = 31 * javaClass.hashCode() + (topic.id?.hashCode() ?: 0)

}