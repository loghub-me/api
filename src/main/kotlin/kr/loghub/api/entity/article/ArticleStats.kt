package kr.loghub.api.entity.article

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class ArticleStats(
    @Column(name = "star_count", nullable = false)
    var starCount: Int = 0,

    @Column(name = "comment_count", nullable = false)
    var commentCount: Int = 0,

    @Column(name = "trending_score", nullable = false)
    var trendingScore: Int = 0,
)