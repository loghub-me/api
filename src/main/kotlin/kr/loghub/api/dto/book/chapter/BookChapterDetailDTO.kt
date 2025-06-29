package kr.loghub.api.dto.book.chapter

import kr.loghub.api.dto.common.ContentDTO

data class BookChapterDetailDTO(
    val id: Long,
    val title: String,
    val content: ContentDTO,
    val sequence: Int,
    val createdAt: String,
    val updatedAt: String,
)