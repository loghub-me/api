package kr.loghub.api.mapper.topic

import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.dto.topic.TopicDetailDTO
import kr.loghub.api.entity.topic.Topic

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