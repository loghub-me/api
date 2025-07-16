package kr.loghub.api.controller.series

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.series.review.PostSeriesReviewDTO
import kr.loghub.api.dto.series.review.SeriesReviewDTO
import kr.loghub.api.dto.response.MethodResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.series.SeriesReviewService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

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
        val review = seriesReviewService.postReview(seriesId, requestBody, writer)
        return MethodResponseBody(
            id = review.id!!,
            message = ResponseMessage.Series.Review.POST_SUCCESS,
            status = HttpStatus.CREATED
        ).toResponseEntity()
    }

    @DeleteMapping("/{reviewId}")
    fun removeReview(
        @PathVariable seriesId: Long,
        @PathVariable reviewId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        seriesReviewService.removeReview(seriesId, reviewId, writer)
        return MethodResponseBody(
            id = reviewId,
            message = ResponseMessage.Series.Review.DELETE_SUCCESS,
            status = HttpStatus.OK
        ).toResponseEntity()
    }
}