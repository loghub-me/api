package kr.loghub.api.dto.question.answer

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.question.QuestionAnswer
import kr.loghub.api.entity.user.User
import kr.loghub.api.lib.validation.Trimmed

data class PostQuestionAnswerDTO(
    @field:NotBlank(message = "제목은 필수 입력 항목입니다.")
    @field:Size(max = 255, message = "제목은 255자 이내여야 합니다.")
    @field:Trimmed
    val title: String,

    @field:NotBlank(message = "내용은 필수 입력 항목입니다.")
    @field:Size(max = 25565, message = "내용은 25,565자 이내여야 합니다.")
    val content: String,
) {
    fun toEntity(question: Question, writer: User) = QuestionAnswer(
        title = title,
        content = content,
        question = question,
        writer = writer,
    )
}