package me.loghub.api.dto.user

data class UserStatsDTO(
    val totalPostedCount: Int,
    val totalAddedStarCount: Int,
    val totalGazedStarCount: Int,
)