package me.loghub.api.dto.question.answer

import me.loghub.api.entity.question.Question
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.entity.user.User
import me.loghub.api.lib.validation.ContentValidation
import me.loghub.api.lib.validation.TitleValidation

data class PostQuestionAnswerDTO(
    @field:TitleValidation
    val title: String,

    @field:ContentValidation
    val content: String,
) {
    fun toEntity(question: Question, writer: User) = QuestionAnswer(
        title = title,
        content = content,
        question = question,
        writer = writer,
    )
}