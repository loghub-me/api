package me.loghub.api.service.support

import me.loghub.api.dto.support.PostSupportInquiryDTO
import me.loghub.api.dto.task.discord.DiscordWebhookSendMessageRequest
import me.loghub.api.entity.support.SupportInquiry
import me.loghub.api.proxy.DiscordWebhookProxy
import me.loghub.api.repository.support.SupportInquiryRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SupportInquiryService(
    private val supportInquiryRepository: SupportInquiryRepository,
    private val discordWebhookProxy: DiscordWebhookProxy,
) {
    companion object {
        const val DISCORD_WEBHOOK_USERNAME = "[LogHub] 문의"
    }

    @Transactional
    fun postInquiry(requestBody: PostSupportInquiryDTO): SupportInquiry {
        val inquiry = requestBody.toEntity()
        val savedInquiry = supportInquiryRepository.save(inquiry)
        sendMessageAsync(requestBody)
        return savedInquiry
    }

    @Async
    fun sendMessageAsync(requestBody: PostSupportInquiryDTO) {
        val embed = requestBody.toDiscordEmbed()
        val message = DiscordWebhookSendMessageRequest(username = DISCORD_WEBHOOK_USERNAME, embeds = listOf(embed))
        discordWebhookProxy.sendMessage(message)
    }
}