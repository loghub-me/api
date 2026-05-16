package me.loghub.api.mapper.series

import me.loghub.api.dto.series.review.SeriesReviewDTO
import me.loghub.api.entity.series.SeriesReview
import me.loghub.api.mapper.user.UserMapper

object SeriesReviewMapper {
    fun map(review: SeriesReview) = SeriesReviewDTO(
        id = review.persistedId,
        content = review.content,
        rating = review.rating,
        writer = UserMapper.map(review.writer),
        createdAt = review.createdAt,
        updatedAt = review.updatedAt,
    )
}