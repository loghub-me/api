package kr.loghub.api.dto.task.answer

data class AnswerGenerateResponse(
    val answerContent: String,
    val rejectionReason: RejectionReason,
) {
    private object RejectionMessage {
        const val OFF_TOPIC = "질문이 주제와 관련이 없거나 질문이 아닙니다."
        const val NOT_ENOUGH_INFO = "질문이 명확하지 않거나 충분한 정보가 제공되지 않았습니다."
    }

    enum class RejectionReason(val message: String) {
        NONE(""),
        OFF_TOPIC(RejectionMessage.OFF_TOPIC),
        NOT_ENOUGH_INFO(RejectionMessage.NOT_ENOUGH_INFO),
    }
}
