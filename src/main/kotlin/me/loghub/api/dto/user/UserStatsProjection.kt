package me.loghub.api.dto.user

interface UserStatsProjection {
    val followerCount: Int
    val followingCount: Int
    val totalPostedCount: Int
    val totalAddedStarCount: Int
    val totalGazedStarCount: Int
}