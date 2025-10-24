package me.loghub.api.dto.article

import me.loghub.api.dto.topic.TopicDTO

data class ArticleForImportDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val topics: List<TopicDTO>,
)
