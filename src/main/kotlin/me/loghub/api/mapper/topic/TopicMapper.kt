package me.loghub.api.mapper.topic

import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.dto.topic.TopicDetailDTO
import me.loghub.api.entity.topic.Topic

object TopicMapper {
    fun map(topic: Topic) = TopicDTO(
        slug = topic.slug,
        name = topic.name
    )

    fun mapDetail(topic: Topic) = TopicDetailDTO(
        slug = topic.slug,
        name = topic.name,
        description = topic.description
    )
}