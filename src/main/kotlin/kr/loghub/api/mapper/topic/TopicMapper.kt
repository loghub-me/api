package kr.loghub.api.mapper.topic

import kr.loghub.api.dto.topic.TopicDTO

object TopicMapper {
    const val TOPIC_DELIMITER = ","
    const val TOPIC_ATTRIBUTE_DELIMITER = ":"

    fun mapTopicsFlat(topicsFlat: String) = topicsFlat
        .split(TOPIC_DELIMITER)
        .map {
            TopicDTO(
                slug = it.substringBefore(TOPIC_ATTRIBUTE_DELIMITER),
                name = it.substringAfter(TOPIC_ATTRIBUTE_DELIMITER)
            )
        }
}