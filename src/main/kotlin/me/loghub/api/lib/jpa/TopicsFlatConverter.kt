package me.loghub.api.lib.jpa

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.entity.topic.Topic

@Converter
class TopicsFlatConverter : AttributeConverter<List<TopicDTO>, String> {
    companion object {
        const val TOPIC_DELIMITER = ","
        const val ATTRIBUTES_DELIMITER = ":"

        fun toFlat(topics: Set<Topic>): List<TopicDTO> = topics.map { TopicDTO(it.slug, it.name) }
    }

    override fun convertToDatabaseColumn(flatTopics: List<TopicDTO>): String {
        if (flatTopics.isEmpty()) {
            return "";
        }

        return flatTopics.joinToString(TOPIC_DELIMITER) { topic ->
            "${topic.slug}${ATTRIBUTES_DELIMITER}${topic.name}"
        }
    }

    override fun convertToEntityAttribute(original: String): List<TopicDTO>? {
        if (original.isEmpty()) {
            return emptyList()
        }

        return original.split(TOPIC_DELIMITER).map { topic ->
            val (slug, name) = topic.split(ATTRIBUTES_DELIMITER)
            TopicDTO(slug, name)
        }
    }
}