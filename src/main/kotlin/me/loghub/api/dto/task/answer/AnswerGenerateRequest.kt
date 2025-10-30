package me.loghub.api.dto.task.answer

import me.loghub.api.constant.ai.LogHubChatModel

data class AnswerGenerateRequest(
    val questionId: Long,
    val questionTitle: String,
    val questionContent: String,
    val chatModel: LogHubChatModel,
    val userInstruction: String?,
)
