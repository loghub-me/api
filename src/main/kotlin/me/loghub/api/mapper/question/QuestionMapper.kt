package me.loghub.api.mapper.question

import me.loghub.api.dto.common.ContentDTO
import me.loghub.api.dto.common.RenderedMarkdownDTO
import me.loghub.api.dto.question.QuestionDTO
import me.loghub.api.dto.question.QuestionDetailDTO
import me.loghub.api.dto.question.QuestionForEditDTO
import me.loghub.api.dto.question.QuestionStatsDTO
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
        writer = UserMapper.map(question.writer),
        stats = mapStats(question.stats),
        topics = question.topicsFlat,
        createdAt = question.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        updatedAt = question.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
    )

    fun mapDetail(question: Question, renderedMarkdown: RenderedMarkdownDTO) =
        QuestionDetailDTO(
            id = question.id!!,
            slug = question.slug,
            title = question.title,
            content = ContentDTO(
                html = renderedMarkdown.html,
                normalized = question.normalizedContent,
            ),
            anchors = renderedMarkdown.anchors,
            status = question.status,
            writer = UserMapper.map(question.writer),
            stats = mapStats(question.stats),
            topics = question.topicsFlat,
            createdAt = question.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
            updatedAt = question.updatedAt.format(DateTimeFormatter.ISO_DATE_TIME),
        )

    fun mapForEdit(question: Question, draft: String?) = QuestionForEditDTO(
        id = question.id!!,
        title = question.title,
        content = question.content,
        draft = draft,
        topicSlugs = question.topicsFlat.map { it.slug },
    )

    private fun mapStats(question: QuestionStats) = QuestionStatsDTO(question.starCount, question.answerCount)
}