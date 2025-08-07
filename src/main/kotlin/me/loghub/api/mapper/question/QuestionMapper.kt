package me.loghub.api.mapper.question

import me.loghub.api.dto.common.ContentDTO
import me.loghub.api.dto.question.QuestionDTO
import me.loghub.api.dto.question.QuestionDetailDTO
import me.loghub.api.dto.question.QuestionStatsDTO
import me.loghub.api.dto.user.UserSimpleDTO
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.question.QuestionStats
import me.loghub.api.mapper.user.UserMapper
import java.time.format.DateTimeFormatter

object QuestionMapper {
    fun map(question: Question) = QuestionDTO(
        id = question.id!!,
        slug = question.slug,
        title = question.title,
        status = question.status,
        writer = UserSimpleDTO(question.writer.id!!, question.writerUsername),
        stats = mapStats(question.stats),
        topics = question.topicsFlat,
        createdAt = question.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = question.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapDetail(question: Question, questionContentHTML: String) =
        QuestionDetailDTO(
            id = question.id!!,
            slug = question.slug,
            title = question.title,
            content = ContentDTO(
                markdown = question.content,
                html = questionContentHTML,
            ),
            status = question.status,
            writer = UserMapper.map(question.writer),
            stats = mapStats(question.stats),
            topics = question.topicsFlat,
            createdAt = question.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
            updatedAt = question.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
        )

    private fun mapStats(question: QuestionStats) = QuestionStatsDTO(question.starCount, question.answerCount)
}