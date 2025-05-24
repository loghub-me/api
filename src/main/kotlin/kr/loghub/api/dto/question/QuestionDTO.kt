package kr.loghub.api.dto.question

import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.entity.question.Question

data class QuestionDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val status: Question.Status,
    val stats: QuestionStatsDTO,
    val writerUsername: String,
    val topics: List<TopicDTO>,
    val createdAt: String,
    val updatedAt: String,
)
