package me.loghub.api.dto.series

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.topic.Topic
import me.loghub.api.entity.user.User
import me.loghub.api.lib.jpa.TopicsFlatConverter
import me.loghub.api.lib.validation.ThumbnailValidation
import me.loghub.api.lib.validation.TitleValidation
import me.loghub.api.lib.validation.TopicSlugsValidation

data class PostSeriesDTO(
    @field:TitleValidation
    val title: String,

    @field:NotBlank(message = "설명은 필수 입력 항목입니다.")
    @field:Size(min = 10, max = 2048, message = "설명은 2048자 이하이어야 합니다.")
    val description: String,

    @field:ThumbnailValidation
    val thumbnail: String,

    @field:TopicSlugsValidation
    val topicSlugs: List<String>,
) {
    fun toEntity(slug: String, writer: User, topics: Set<Topic>) = Series(
        slug = slug,
        title = title,
        description = description,
        thumbnail = thumbnail,
        writer = writer,
        writerUsername = writer.username,
        topics = topics.toMutableSet(),
        topicsFlat = TopicsFlatConverter.toFlat(topics),
    )
}