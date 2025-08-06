package kr.loghub.api.dto.series

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kr.loghub.api.constant.validation.RegexExpression
import kr.loghub.api.entity.series.Series
import kr.loghub.api.entity.topic.Topic
import kr.loghub.api.entity.user.User
import kr.loghub.api.lib.jpa.TopicsFlatConverter
import kr.loghub.api.lib.validation.Trimmed

data class PostSeriesDTO(
    @field:NotBlank(message = "제목은 필수 입력 항목입니다.")
    @field:Size(min = 2, max = 56, message = "제목은 2자 이상 56자 이하이어야 합니다.")
    @field:Trimmed
    val title: String,

    @field:NotBlank(message = "내용은 필수 입력 항목입니다.")
    @field:Size(min = 10, max = 8192, message = "내용은 10자 이상 8192자 이하이어야 합니다.")
    val content: String,

    @field:NotBlank(message = "썸네일은 필수 입력 항목입니다.")
    @field:Pattern(regexp = RegexExpression.THUMBNAIL, message = "올바르지 않은 썸네일 형식입니다.")
    @field:Trimmed
    val thumbnail: String,

    @field:NotNull(message = "토픽은 필수 입력 항목입니다.")
    @field:Size(max = 10, message = "토픽은 최대 10개까지 선택할 수 있습니다.")
    val topicSlugs: List<String>,
) {
    fun toEntity(slug: String, writer: User, topics: Set<Topic>) = Series(
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