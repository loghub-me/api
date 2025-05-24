package kr.loghub.api.dto.question

import kr.loghub.api.dto.common.ContentDTO
import kr.loghub.api.dto.question.answer.AnswerDTO
import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.dto.user.UserDTO
import kr.loghub.api.entity.question.Question

data class QuestionDetailDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val content: ContentDTO,
    val status: Question.Status,
    val writer: UserDTO,
    val stats: QuestionStatsDTO,
    val topics: List<TopicDTO>,
    val answers: List<AnswerDTO>,
    val createdAt: String,
    val updatedAt: String,
)
