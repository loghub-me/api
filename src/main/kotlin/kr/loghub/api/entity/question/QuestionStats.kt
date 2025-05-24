package kr.loghub.api.entity.question

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class QuestionStats(
    @Column(name = "star_count", nullable = false)
    var starCount: Int = 0,

    @Column(name = "answer_count", nullable = false)
    var answerCount: Int = 0,

    @Column(name = "trending_score", nullable = false)
    var trendingScore: Int = 0,
)