package me.loghub.api.entity.series

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class SeriesStats(
    @Column(name = "star_count", nullable = false)
    var starCount: Int = 0,

    @Column(name = "review_count", nullable = false)
    var reviewCount: Int = 0,

    @Column(name = "trending_score", nullable = false)
    var trendingScore: Int = 0,
)