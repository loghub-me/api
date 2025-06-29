package kr.loghub.api.dto.book.review

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kr.loghub.api.entity.book.Book
import kr.loghub.api.entity.book.BookReview
import kr.loghub.api.entity.user.User

data class PostBookReviewDTO(
    @field:NotBlank(message = "내용은 필수 입력 항목입니다.")
    @field:Size(max = 512, message = "내용은 512자 이내여야 합니다.")
    val content: String,

    @field:Min(1, message = "평점은 1 이상이어야 합니다.")
    @field:Max(5, message = "평점은 5 이하여야 합니다.")
    val rating: Int = 5,
) {
    fun toEntity(book: Book, writer: User) = BookReview(
        content = content,
        rating = rating,
        book = book,
        writer = writer,
    )
}