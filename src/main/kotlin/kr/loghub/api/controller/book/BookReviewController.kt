package kr.loghub.api.controller.book

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.book.review.BookReviewDTO
import kr.loghub.api.dto.book.review.PostBookReviewDTO
import kr.loghub.api.dto.response.MethodResponseBody
import kr.loghub.api.dto.response.ResponseBody
import kr.loghub.api.entity.user.User
import kr.loghub.api.service.book.BookReviewService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/books/{bookId}/reviews")
class BookReviewController(private val bookReviewService: BookReviewService) {
    @GetMapping
    fun getReviews(
        @PathVariable bookId: Long,
        @RequestParam(defaultValue = "1") page: Int,
    ): ResponseEntity<Page<BookReviewDTO>> {
        val reviews = bookReviewService.getReviews(bookId, page)
        return ResponseEntity.ok(reviews)
    }

    @PostMapping
    fun postReview(
        @PathVariable bookId: Long,
        @RequestBody @Valid requestBody: PostBookReviewDTO,
        @AuthenticationPrincipal writer: User,
    ): ResponseEntity<ResponseBody> {
        val review = bookReviewService.postReview(bookId, requestBody, writer)
        return MethodResponseBody(
            id = review.id!!,
            message = ResponseMessage.Book.Review.POST_SUCCESS,
            status = HttpStatus.CREATED
        ).toResponseEntity()
    }

    @DeleteMapping("/{reviewId}")
    fun removeReview(
        @PathVariable bookId: Long,
        @PathVariable reviewId: Long,
        @AuthenticationPrincipal writer: User
    ): ResponseEntity<ResponseBody> {
        bookReviewService.removeReview(bookId, reviewId, writer)
        return MethodResponseBody(
            id = reviewId,
            message = ResponseMessage.Book.Review.DELETE_SUCCESS,
            status = HttpStatus.OK
        ).toResponseEntity()
    }
}