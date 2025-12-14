package me.loghub.api.dto.article

import me.loghub.api.dto.topic.TopicDTO

data class ArticleUnpublishedDTO(
    val id: Long,
    val title: String,
    val topics: List<TopicDTO>,
    val createdAt: String,
)
