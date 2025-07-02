package kr.loghub.api.dto.question.answer

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.question.QuestionAnswer
import kr.loghub.api.entity.user.User

data class PostQuestionAnswerDTO(
    @field:NotBlank(message = "내용은 필수 입력 항목입니다.")
    @field:Size(max = 25565, message = "내용은 25,565자 이내여야 합니다.")
    val content: String,
) {
    fun toEntity(question: Question, writer: User) = QuestionAnswer(
        content = content,
        question = question,
        writer = writer,
    )
}