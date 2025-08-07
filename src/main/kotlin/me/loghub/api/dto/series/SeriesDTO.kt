package me.loghub.api.dto.series

import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.dto.user.UserSimpleDTO

data class SeriesDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val thumbnail: String,
    val stats: SeriesStatsDTO,
    val writer: UserSimpleDTO,
    val topics: List<TopicDTO>,
    val createdAt: String,
    val updatedAt: String,
)
