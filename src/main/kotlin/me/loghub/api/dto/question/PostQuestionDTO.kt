package me.loghub.api.dto.question

import me.loghub.api.entity.question.Question
import me.loghub.api.entity.topic.Topic
import me.loghub.api.entity.user.User
import me.loghub.api.lib.jpa.TopicsFlatConverter
import me.loghub.api.lib.validation.ContentValidation
import me.loghub.api.lib.validation.TitleValidation
import me.loghub.api.lib.validation.TopicSlugsValidation

data class PostQuestionDTO(
    @field:TitleValidation
    val title: String,

    @field:ContentValidation
    val content: String,

    @field:TopicSlugsValidation
    val topicSlugs: List<String>
) {
    fun toEntity(slug: String, normalizedContent: String, writer: User, topics: Set<Topic>) = Question(
        slug = slug,
        title = title,
        content = content,
        normalizedContent = normalizedContent,
        writer = writer,
        writerUsername = writer.username,
        topics = topics.toMutableSet(),
        topicsFlat = TopicsFlatConverter.toFlat(topics),
    )
}