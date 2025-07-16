package kr.loghub.api.dto.article

import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.dto.user.UserSimpleDTO

data class ArticleDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val thumbnail: String,
    val stats: ArticleStatsDTO,
    val writer: UserSimpleDTO,
    val topics: List<TopicDTO>,
    val createdAt: String,
    val updatedAt: String,
)
