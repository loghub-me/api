package kr.loghub.api.dto.book.chapter

data class BookChapterDTO(
    val id: Long,
    val title: String,
    val sequence: Int,
    val createdAt: String,
    val updatedAt: String,
)