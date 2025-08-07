package me.loghub.api.dto.series.review

import me.loghub.api.dto.user.UserDTO

data class SeriesReviewDTO(
    val id: Long,
    val content: String,
    val rating: Int,
    val writer: UserDTO,
    val createdAt: String,
    val updatedAt: String,
)