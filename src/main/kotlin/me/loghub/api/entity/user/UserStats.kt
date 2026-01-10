package me.loghub.api.entity.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import me.loghub.api.dto.user.UserStatsProjection

@Embeddable
data class UserStats(
    @Column(nullable = false)
    val totalPostedCount: Int = 0,

    @Column(nullable = false)
    val totalAddedStarCount: Int = 0,

    @Column(nullable = false)
    val totalGazedStarCount: Int = 0,
) {
    constructor(projection: UserStatsProjection) : this(
        totalPostedCount = projection.totalPostedCount,
        totalAddedStarCount = projection.totalAddedStarCount,
        totalGazedStarCount = projection.totalGazedStarCount,
    )
}