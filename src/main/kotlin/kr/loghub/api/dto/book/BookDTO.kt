package kr.loghub.api.dto.book

import kr.loghub.api.dto.topic.TopicDTO

data class BookDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val thumbnail: String,
    val stats: BookStatsDTO,
    val writerUsername: String,
    val topics: List<TopicDTO>,
    val createdAt: String,
    val updatedAt: String,
)
