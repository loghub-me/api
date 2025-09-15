package me.loghub.api.dto.article

import me.loghub.api.dto.common.AnchorDTO
import me.loghub.api.dto.common.ContentDTO
import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.dto.user.UserDetailDTO

data class ArticleDetailDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val content: ContentDTO,
    val anchors: List<AnchorDTO>,
    val thumbnail: String,
    val writer: UserDetailDTO,
    val stats: ArticleStatsDTO,
    val topics: List<TopicDTO>,
    val createdAt: String,
    val updatedAt: String,
)
