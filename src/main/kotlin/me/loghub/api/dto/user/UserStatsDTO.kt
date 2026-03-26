package me.loghub.api.dto.user

import me.loghub.api.dto.topic.TopicUsageDTO

data class UserStatsDTO(
    val followersCount: Int,
    val followingCount: Int,
    val totalPostedCount: Int,
    val totalAddedStarCount: Int,
    val totalGazedStarCount: Int,
    val topicUsages: List<TopicUsageDTO>,
)