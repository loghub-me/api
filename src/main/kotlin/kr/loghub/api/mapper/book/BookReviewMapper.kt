package kr.loghub.api.mapper.book

import kr.loghub.api.dto.book.review.BookReviewDTO
import kr.loghub.api.entity.book.BookReview
import kr.loghub.api.mapper.user.UserMapper
import java.time.format.DateTimeFormatter

object BookReviewMapper {
    fun map(review: BookReview) = BookReviewDTO(
        id = review.id!!,
        content = review.content,
        rating = review.rating,
        writer = UserMapper.map(review.writer),
        createdAt = review.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = review.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )
}