package me.loghub.api.mapper.series

import me.loghub.api.dto.series.review.SeriesReviewDTO
import me.loghub.api.entity.series.SeriesReview
import me.loghub.api.mapper.user.UserMapper
import java.time.format.DateTimeFormatter

object SeriesReviewMapper {
    fun map(review: SeriesReview) = SeriesReviewDTO(
        id = review.id!!,
        content = review.content,
        rating = review.rating,
        writer = UserMapper.map(review.writer),
        createdAt = review.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = review.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )
}