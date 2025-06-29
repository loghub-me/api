package kr.loghub.api.dto.book

import kr.loghub.api.dto.book.chapter.BookChapterDTO
import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.dto.user.UserDTO

data class BookDetailDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val content: String,
    val thumbnail: String,
    val writer: UserDTO,
    val stats: BookStatsDTO,
    val topics: List<TopicDTO>,
    val chapters: List<BookChapterDTO>,
    val createdAt: String,
    val updatedAt: String,
)
