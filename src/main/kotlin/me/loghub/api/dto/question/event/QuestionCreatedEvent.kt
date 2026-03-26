package me.loghub.api.dto.question.event

data class QuestionCreatedEvent(
    val questionId: Long,
    val writerId: Long,
)
