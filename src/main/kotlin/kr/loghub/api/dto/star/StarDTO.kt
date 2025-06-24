package kr.loghub.api.dto.star

import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.entity.star.Star

data class StarDTO(
    val id: Long,
    val path: String,
    val title: String,
    val writerUsername: String,
    val topics: List<TopicDTO>,
    val createdAt: String,
    val target: Star.Target,
    val targetLabel: String,
)