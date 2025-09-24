package me.loghub.api.proxy

import me.loghub.api.dto.task.discord.DiscordWebhookSendMessageRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "discord-webhook",
    url = "\${discord-webhook.url}",
)
interface DiscordWebhookProxy {
    @PostMapping
    fun sendMessage(@RequestBody request: DiscordWebhookSendMessageRequest)
}