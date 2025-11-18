package me.loghub.api.service.common

import com.resend.Resend
import com.resend.services.emails.model.CreateEmailOptions
import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.config.AsyncConfig
import me.loghub.api.config.ResendConfig
import me.loghub.api.dto.task.mail.MailSendRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class MailService(private val resendClient: Resend) {
    private companion object {
        val logger = KotlinLogging.logger { };
    }

    @Async(AsyncConfig.MailExecutor.NAME)
    fun sendMailAsync(request: MailSendRequest) {
        val params = CreateEmailOptions.builder()
            .from(ResendConfig.FROM)
            .to(request.to)
            .subject(request.subject)
            .html(request.html)
            .build()

        try {
            resendClient.emails().send(params)
        } catch (e: RuntimeException) {
            logger.error(e) { "Resend client error: ${e.message}" }
        }
    }
}