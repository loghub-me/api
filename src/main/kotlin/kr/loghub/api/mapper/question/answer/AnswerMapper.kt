package kr.loghub.api.mapper.question.answer

import kr.loghub.api.dto.common.ContentDTO
import kr.loghub.api.dto.question.answer.AnswerDTO
import kr.loghub.api.entity.question.Answer
import java.time.format.DateTimeFormatter

object AnswerMapper {
    fun map(answer: Answer, contentHTML: String) = AnswerDTO(
        id = answer.id ?: TODO(),
        content = ContentDTO(
            markdown = answer.content,
            html = contentHTML,
        ),
        accepted = answer.accepted,
        writerUsername = answer.writer.username,
        createdAt = answer.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = answer.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )
}