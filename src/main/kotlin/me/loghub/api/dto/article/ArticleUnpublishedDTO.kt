package me.loghub.api.dto.article

import me.loghub.api.dto.topic.TopicDTO
import java.time.OffsetDateTime

data class ArticleUnpublishedDTO(
    val id: Long,
    val title: String,
    val topics: List<TopicDTO>,
    val createdAt: OffsetDateTime,
)
