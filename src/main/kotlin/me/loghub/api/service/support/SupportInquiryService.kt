package me.loghub.api.service.support

import me.loghub.api.dto.support.PostSupportInquiryDTO
import me.loghub.api.dto.task.discord.DiscordWebhookSendMessageRequest
import me.loghub.api.proxy.DiscordWebhookProxy
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SupportInquiryService(private val discordWebhookProxy: DiscordWebhookProxy) {
    companion object {
        const val DISCORD_WEBHOOK_USERNAME = "[LogHub] 문의"
    }

    @Transactional
    fun postInquiry(requestBody: PostSupportInquiryDTO) {
        val embed = requestBody.toDiscordEmbed()
        val message = DiscordWebhookSendMessageRequest(username = DISCORD_WEBHOOK_USERNAME, embeds = listOf(embed))
        sendMessageAsync(message)
    }

    @Async
    fun sendMessageAsync(message: DiscordWebhookSendMessageRequest) {
        discordWebhookProxy.sendMessage(message)
    }
}