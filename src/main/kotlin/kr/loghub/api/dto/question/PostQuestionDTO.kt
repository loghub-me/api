package kr.loghub.api.dto.question

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.topic.Topic
import kr.loghub.api.entity.user.User
import kr.loghub.api.lib.jpa.TopicsFlatConverter
import kr.loghub.api.lib.validation.Trimmed

data class PostQuestionDTO(
    @field:NotBlank(message = "제목은 필수 입력 항목입니다.")
    @field:Size(max = 255, message = "제목은 255자 이내여야 합니다.")
    @field:Trimmed
    val title: String,

    @field:NotBlank(message = "내용은 필수 입력 항목입니다.")
    @field:Size(max = 25565, message = "내용은 25,565자 이내여야 합니다.")
    val content: String,

    @field:NotNull(message = "토픽은 필수 입력 항목입니다.")
    @field:Size(max = 10, message = "토픽은 최대 10개까지 선택할 수 있습니다.")
    val topicSlugs: List<String>
) {
    fun toEntity(slug: String, writer: User, topics: Set<Topic>) = Question(
        slug = slug,
        title = title,
        content = content,
        writer = writer,
        writerUsername = writer.username,
        topics = topics.toMutableSet(),
        topicsFlat = TopicsFlatConverter.toFlat(topics),
    )
}