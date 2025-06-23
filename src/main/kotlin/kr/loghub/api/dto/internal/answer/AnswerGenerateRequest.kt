package kr.loghub.api.dto.internal.answer

data class AnswerGenerateRequest(
    val questionId: Long,
    val questionContent: String,
)
