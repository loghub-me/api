package me.loghub.api.dto.user

interface UserStatsProjection {
    val totalPostedCount: Int;
    val totalAddedStarCount: Int;
    val totalGazedStarCount: Int;
}