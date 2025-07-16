package kr.loghub.api.dto.series

import kr.loghub.api.dto.series.chapter.SeriesChapterDTO
import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.dto.user.UserDTO

data class SeriesDetailDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val content: String,
    val thumbnail: String,
    val writer: UserDTO,
    val stats: SeriesStatsDTO,
    val topics: List<TopicDTO>,
    val chapters: List<SeriesChapterDTO>,
    val createdAt: String,
    val updatedAt: String,
)
