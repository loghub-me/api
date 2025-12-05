package me.loghub.api.dto.article

import me.loghub.api.entity.article.Article
import me.loghub.api.entity.topic.Topic
import me.loghub.api.entity.user.User
import me.loghub.api.lib.jpa.TopicsFlatConverter
import me.loghub.api.lib.validation.ContentValidation
import me.loghub.api.lib.validation.ThumbnailValidation
import me.loghub.api.lib.validation.TitleValidation
import me.loghub.api.lib.validation.TopicSlugsValidation

data class PostArticleDTO(
    @field:TitleValidation
    val title: String,

    @field:ContentValidation
    val content: String,

    @field:ThumbnailValidation
    val thumbnail: String,

    @field:TopicSlugsValidation
    val topicSlugs: List<String>,
) {
    fun toEntity(slug: String, writer: User, topics: Set<Topic>) = Article(
        slug = slug,
        title = title,
        content = content,
        thumbnail = thumbnail,
        writer = writer,
        writerUsername = writer.username,
        topics = topics.toMutableSet(),
        topicsFlat = TopicsFlatConverter.toFlat(topics),
    )
}