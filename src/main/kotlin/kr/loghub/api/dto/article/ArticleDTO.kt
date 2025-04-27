package kr.loghub.api.dto.article

import kr.loghub.api.dto.topic.TopicDTO

data class ArticleDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val thumbnail: String,
    val stats: ArticleStatsDTO,
    val writerUsername: String,
    val topics: List<TopicDTO>,
    val createdAt: String,
    val updatedAt: String,
)
