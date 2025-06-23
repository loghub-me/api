package kr.loghub.api.dto.internal.answer

data class AnswerGenerateResponse(
    val answerContent: String,
    val rejectionReason: RejectionReason,
) {
    enum class RejectionReason {
        NONE,
        OFF_TOPIC,
        NOT_ENOUGH_INFO,
    }
}
