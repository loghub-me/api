package me.loghub.api.dto.article

import jakarta.validation.constraints.NotNull
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.topic.Topic
import me.loghub.api.entity.user.User
import me.loghub.api.lib.jpa.TopicsFlatConverter
import me.loghub.api.lib.validation.ContentValidation
import me.loghub.api.lib.validation.ThumbnailValidation
import me.loghub.api.lib.validation.TitleValidation
import me.loghub.api.lib.validation.TopicSlugsValidation
import java.time.LocalDateTime

data class PostArticleDTO(
    @field:TitleValidation
    val title: String,

    @field:ContentValidation
    val content: String,

    @field:ThumbnailValidation
    val thumbnail: String,

    @field:TopicSlugsValidation
    val topicSlugs: List<String>,

    @field:NotNull(message = "공개 여부는 필수 입력값입니다.")
    val published: Boolean,
) {
    fun toEntity(slug: String, writer: User, topics: Set<Topic>) = Article(
        slug = slug,
        title = title,
        content = content,
        thumbnail = thumbnail,
        published = published,
        publishedAt = if (published) LocalDateTime.now() else null,
        writer = writer,
        writerUsername = writer.username,
        topics = topics.toMutableSet(),
        topicsFlat = TopicsFlatConverter.toFlat(topics),
    )
}