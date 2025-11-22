package me.loghub.api.service.support

import me.loghub.api.dto.support.PostSupportInquiryDTO
import me.loghub.api.dto.support.PostSupportTopicRequestDTO
import me.loghub.api.dto.task.discord.DiscordWebhookSendMessageRequest
import me.loghub.api.proxy.DiscordWebhookProxy
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SupportService(private val discordWebhookProxy: DiscordWebhookProxy) {
    private object DiscordWebhookUsername {
        const val INQUIRY = "[LogHub] 문의"
        const val TOPIC_REQUEST = "[LogHub] 토픽 요청"
    }

    @Transactional
    fun postInquiry(requestBody: PostSupportInquiryDTO) {
        val embed = requestBody.toDiscordEmbed()
        val message =
            DiscordWebhookSendMessageRequest(username = DiscordWebhookUsername.INQUIRY, embeds = listOf(embed))
        sendMessageAsync(message)
    }

    @Transactional
    fun postTopicRequest(requestBody: PostSupportTopicRequestDTO) {
        val embed = requestBody.toDiscordEmbed()
        val message =
            DiscordWebhookSendMessageRequest(username = DiscordWebhookUsername.TOPIC_REQUEST, embeds = listOf(embed))
        sendMessageAsync(message)
    }

    @Async
    fun sendMessageAsync(message: DiscordWebhookSendMessageRequest) {
        discordWebhookProxy.sendMessage(message)
    }
}