package me.loghub.api.constant.ai

import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.openai.api.OpenAiApi

enum class LogHubChatModel(
    val modelName: String,
    val temperature: Double,
) {
    GPT_4_1_MINI(OpenAiApi.ChatModel.GPT_4_1_MINI.value, 0.7),
    GPT_5(OpenAiApi.ChatModel.GPT_5_CHAT_LATEST.value, 1.0),
    O3(OpenAiApi.ChatModel.O3.value, 0.7);

    fun toChatOptions(): ChatOptions = ChatOptions.builder()
        .model(modelName)
        .temperature(temperature)
        .build()
}