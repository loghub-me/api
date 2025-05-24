package kr.loghub.api.mapper.question

import kr.loghub.api.dto.common.ContentDTO
import kr.loghub.api.dto.question.QuestionDTO
import kr.loghub.api.dto.question.QuestionDetailDTO
import kr.loghub.api.dto.question.QuestionStatsDTO
import kr.loghub.api.entity.question.Question
import kr.loghub.api.entity.question.QuestionStats
import kr.loghub.api.mapper.question.answer.AnswerMapper
import kr.loghub.api.mapper.user.UserMapper
import java.time.format.DateTimeFormatter

object QuestionMapper {
    fun map(question: Question) = QuestionDTO(
        id = question.id ?: TODO(),
        slug = question.slug,
        title = question.title,
        status = question.status,
        writerUsername = question.writerUsername,
        stats = mapStats(question.stats),
        topics = question.topicsFlat,
        createdAt = question.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = question.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapDetail(question: Question, questionContentHTML: String, answerContentHTMLs: List<String>) =
        QuestionDetailDTO(
            id = question.id ?: TODO(),
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
            answers = question.answers.mapIndexed { i, answer ->
                AnswerMapper.map(answer, answerContentHTMLs[i])
            },
            createdAt = question.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
            updatedAt = question.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
        )

    private fun mapStats(question: QuestionStats) = QuestionStatsDTO(question.starCount, question.answerCount)
}