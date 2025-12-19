package me.loghub.api.controller.series

import jakarta.validation.Valid
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.response.MethodResponseBody
import me.loghub.api.dto.response.ResponseBody
import me.loghub.api.dto.series.review.PostSeriesReviewDTO
import me.loghub.api.dto.series.review.SeriesReviewDTO
import me.loghub.api.entity.user.User
import me.loghub.api.service.series.SeriesReviewService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/series/{seriesId}/reviews")
class SeriesReviewController(private val seriesReviewService: SeriesReviewService) {
    @GetMapping
    fun getReviews(
        @PathVariable seriesId: Long,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<SeriesReviewDTO>> {
        val reviews = seriesReviewService.getReviews(seriesId, page)
        return ResponseEntity.ok(reviews)
    }

    @PostMapping
    fun postReview(
        @PathVariable seriesId: Long,
        @RequestBody @Valid requestBody: PostSeriesReviewDTO,
        @AuthenticationPrincipal writer: User,
    ): ResponseEntity<ResponseBody> {
        val postedReview = seriesReviewService.postReview(seriesId, requestBody, writer)
        return MethodResponseBody(
            id = postedReview.id!!,
            message = ResponseMessage.Series.Review.POST_SUCCESS,
            status = HttpStatus.CREATED
        ).toResponseEntity()
    }

    @PutMapping("/{reviewId}")
    fun editReview(
        @PathVariable seriesId: Long,
        @PathVariable reviewId: Long,
        @RequestBody @Valid requestBody: PostSeriesReviewDTO,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        val editedReview = seriesReviewService.editReview(seriesId, reviewId, requestBody, writer)
        return MethodResponseBody(
            id = editedReview.id!!,
            message = ResponseMessage.Series.Review.EDIT_SUCCESS,
            status = HttpStatus.OK
        ).toResponseEntity()
    }

    @DeleteMapping("/{reviewId}")
    fun deleteReview(
        @PathVariable seriesId: Long,
        @PathVariable reviewId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        seriesReviewService.deleteReview(seriesId, reviewId, writer)
        return MethodResponseBody(
            id = reviewId,
            message = ResponseMessage.Series.Review.DELETE_SUCCESS,
            status = HttpStatus.OK
        ).toResponseEntity()
    }
}