package kr.loghub.api.dto.book.review

import kr.loghub.api.dto.user.UserDTO

data class BookReviewDTO(
    val id: Long,
    val content: String,
    val rating: Int,
    val writer: UserDTO,
    val createdAt: String,
    val updatedAt: String,
)