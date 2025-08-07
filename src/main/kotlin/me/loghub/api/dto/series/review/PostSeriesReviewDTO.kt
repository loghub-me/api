package me.loghub.api.dto.series.review

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.series.SeriesReview
import me.loghub.api.entity.user.User

data class PostSeriesReviewDTO(
    @field:NotBlank(message = "내용은 필수 입력 항목입니다.")
    @field:Size(max = 255, message = "내용은 255자 이하이어야 합니다.")
    val content: String,

    @field:Min(1, message = "평점은 1점 이상이어야 합니다.")
    @field:Max(5, message = "평점은 5점 이하이어야 합니다.")
    val rating: Int = 5,
) {
    fun toEntity(series: Series, writer: User) = SeriesReview(
        content = content,
        rating = rating,
        series = series,
        writer = writer,
    )
}