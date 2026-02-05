package me.loghub.api.dto.user

import me.loghub.api.dto.topic.TopicUsageDTO

data class UserStatsDTO(
    val totalPostedCount: Int,
    val totalAddedStarCount: Int,
    val totalGazedStarCount: Int,
    val topicUsages: List<TopicUsageDTO>,
)