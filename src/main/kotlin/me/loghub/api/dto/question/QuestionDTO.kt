package me.loghub.api.dto.question

import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.dto.user.UserSimpleDTO
import me.loghub.api.entity.question.Question

data class QuestionDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val status: Question.Status,
    val stats: QuestionStatsDTO,
    val writer: UserSimpleDTO,
    val topics: List<TopicDTO>,
    val createdAt: String,
    val updatedAt: String,
)
