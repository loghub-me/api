package me.loghub.api.dto.task.answer

data class AnswerGenerateRequest(
    val questionId: Long,
    val questionTitle: String,
    val questionContent: String,
)
