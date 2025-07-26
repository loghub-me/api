package kr.loghub.api.mapper.question

import kr.loghub.api.dto.common.ContentDTO
import kr.loghub.api.dto.question.answer.QuestionAnswerDTO
import kr.loghub.api.entity.question.QuestionAnswer
import kr.loghub.api.mapper.user.UserMapper
import java.time.format.DateTimeFormatter

object QuestionAnswerMapper {
    fun map(answer: QuestionAnswer, contentHTML: String) = QuestionAnswerDTO(
        id = answer.id!!,
        title = answer.title,
        content = ContentDTO(
            markdown = answer.content,
            html = contentHTML,
        ),
        accepted = answer.accepted,
        writer = UserMapper.map(answer.writer),
        createdAt = answer.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = answer.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )
}