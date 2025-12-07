package me.loghub.api.mapper.question

import me.loghub.api.dto.common.ContentDTO
import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.dto.question.answer.QuestionAnswerDTO
import me.loghub.api.dto.question.answer.QuestionAnswerForEditDTO
import me.loghub.api.entity.question.QuestionAnswer
import me.loghub.api.mapper.user.UserMapper
import java.time.format.DateTimeFormatter

object QuestionAnswerMapper {
    fun map(answer: QuestionAnswer, renderedMarkdown: RenderedMarkdownDTO) = QuestionAnswerDTO(
        id = answer.id!!,
        title = answer.title,
        content = ContentDTO(
            markdown = answer.content,
            html = renderedMarkdown.html,
        ),
        anchors = renderedMarkdown.anchors,
        accepted = answer.accepted,
        writer = UserMapper.map(answer.writer),
        createdAt = answer.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = answer.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapForEdit(answer: QuestionAnswer, draft: String?) = QuestionAnswerForEditDTO(
        id = answer.id!!,
        title = answer.title,
        content = answer.content,
        draft = draft,
    )
}