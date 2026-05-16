package me.loghub.api.dto.article

import me.loghub.api.dto.common.AnchorDTO
import me.loghub.api.dto.common.ContentDTO
import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.dto.user.UserDTO
import java.time.OffsetDateTime

data class ArticleDetailDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val content: ContentDTO,
    val anchors: List<AnchorDTO>,
    val thumbnail: String,
    val writer: UserDTO,
    val stats: ArticleStatsDTO,
    val topics: List<TopicDTO>,
    val publishedAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)
