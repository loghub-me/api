package me.loghub.api.lib.jpa

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import me.loghub.api.dto.topic.TopicUsageDTO

@Converter
class TopicsUsageConverter : AttributeConverter<List<TopicUsageDTO>, String> {
    companion object {
        const val TOPIC_DELIMITER = ","
        const val ATTRIBUTES_DELIMITER = ":"
    }

    override fun convertToDatabaseColumn(flatTopics: List<TopicUsageDTO>): String {
        if (flatTopics.isEmpty()) {
            return "";
        }

        return flatTopics.joinToString(TOPIC_DELIMITER) { topic ->
            "${topic.slug}${ATTRIBUTES_DELIMITER}${topic.name}${ATTRIBUTES_DELIMITER}${topic.count}"
        }
    }

    override fun convertToEntityAttribute(original: String): List<TopicUsageDTO> {
        if (original.isEmpty()) {
            return emptyList()
        }

        return original.split(TOPIC_DELIMITER).map { topic ->
            val (slug, name, count) = topic.split(ATTRIBUTES_DELIMITER)
            TopicUsageDTO(slug, name, Integer.valueOf(count))
        }
    }
}
