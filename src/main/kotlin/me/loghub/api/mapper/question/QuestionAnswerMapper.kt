package me.loghub.api.mapper.question

import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.dto.question.answer.QuestionAnswerDTO
import me.loghub.api.dto.question.answer.QuestionAnswerForEditDTO
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.mapper.user.UserMapper

object QuestionAnswerMapper {
    fun map(answer: QuestionAnswer, renderedMarkdown: RenderedMarkdownDTO) = QuestionAnswerDTO(
        id = answer.persistedId,
        title = answer.title,
        contentHTML = renderedMarkdown.html,
        anchors = renderedMarkdown.anchors,
        accepted = answer.accepted,
        writer = UserMapper.map(answer.writer),
        createdAt = answer.createdAt,
        updatedAt = answer.updatedAt,
    )

    fun mapForEdit(answer: QuestionAnswer, draft: String?) = QuestionAnswerForEditDTO(
        id = answer.persistedId,
        title = answer.title,
        content = answer.content,
        draft = draft,
    )
}