package me.loghub.api.dto.task.discord

data class DiscordWebhookSendMessageRequest(
    val username: String,
    val embeds: List<DiscordEmbed>
)