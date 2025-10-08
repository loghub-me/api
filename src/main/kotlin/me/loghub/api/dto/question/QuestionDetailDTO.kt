package me.loghub.api.dto.question

import me.loghub.api.dto.common.AnchorDTO
import me.loghub.api.dto.common.ContentDTO
import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.dto.user.UserDetailDTO
import me.loghub.api.entity.question.Question

data class QuestionDetailDTO(
    val id: Long,
    val slug: String,
    val title: String,
    val content: ContentDTO,
    val anchors: List<AnchorDTO>,
    val status: Question.Status,
    val answerGenerating: Boolean,
    val writer: UserDetailDTO,
    val stats: QuestionStatsDTO,
    val topics: List<TopicDTO>,
    val createdAt: String,
    val updatedAt: String,
)
