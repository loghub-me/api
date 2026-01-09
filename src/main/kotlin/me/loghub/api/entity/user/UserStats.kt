package me.loghub.api.entity.user

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class UserStats(
    @Column(nullable = false)
    val totalPostedCount: Int = 0,

    @Column(nullable = false)
    val totalAddedStarCount: Int = 0,

    @Column(nullable = false)
    val totalGazedStarCount: Int = 0,
)
