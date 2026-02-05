package me.loghub.api.entity.user

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import me.loghub.api.dto.topic.TopicUsageDTO
import me.loghub.api.dto.topic.TopicUsageProjection
import me.loghub.api.dto.user.UserStatsProjection
import me.loghub.api.lib.jpa.TopicsUsageConverter

@Embeddable
data class UserStats(
    @Column(nullable = false)
    val totalPostedCount: Int = 0,

    @Column(nullable = false)
    val totalAddedStarCount: Int = 0,

    @Column(nullable = false)
    val totalGazedStarCount: Int = 0,

    @Column(nullable = false)
    @Convert(converter = TopicsUsageConverter::class)
    val topicUsages: List<TopicUsageDTO> = emptyList(),
) {
    constructor(stats: UserStatsProjection, topicUsages: List<TopicUsageProjection>) : this(
        totalPostedCount = stats.totalPostedCount,
        totalAddedStarCount = stats.totalAddedStarCount,
        totalGazedStarCount = stats.totalGazedStarCount,
        topicUsages = topicUsages.map { TopicUsageDTO(it) }
    )
}