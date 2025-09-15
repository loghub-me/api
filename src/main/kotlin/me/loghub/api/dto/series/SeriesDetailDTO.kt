package me.loghub.api.dto.series

import me.loghub.api.dto.series.chapter.SeriesChapterDTO
import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.dto.user.UserDetailDTO

data class SeriesDetailDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val description: String,
    val thumbnail: String,
    val writer: UserDetailDTO,
    val stats: SeriesStatsDTO,
    val topics: List<TopicDTO>,
    val chapters: List<SeriesChapterDTO>,
    val createdAt: String,
    val updatedAt: String,
)
