package me.loghub.api.service.email

import com.resend.Resend
import com.resend.services.emails.model.CreateEmailOptions
import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.config.AsyncConfig
import me.loghub.api.dto.config.ResendProperties
import me.loghub.api.dto.task.email.EmailSendRequest
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
@Profile("prod")
class ResendEmailService(
    private val resend: Resend,
    private val props: ResendProperties,
) : EmailService {
    private companion object {
        val logger = KotlinLogging.logger { };
    }

    @Async(AsyncConfig.MailExecutor.NAME)
    override fun sendEmailAsync(request: EmailSendRequest) {
        val params = CreateEmailOptions.builder()
            .from(props.from)
            .to(request.to)
            .subject(request.subject)
            .html(request.html)
            .build()

        try {
            resend.emails().send(params)
        } catch (e: RuntimeException) {
            logger.error(e) { "Resend client error: ${e.message}" }
        }
    }
}