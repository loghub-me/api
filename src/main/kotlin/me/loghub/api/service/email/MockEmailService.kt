package me.loghub.api.service.email

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.config.AsyncConfig
import me.loghub.api.dto.task.email.EmailSendRequest
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
@Profile("dev")
class MockEmailService : EmailService {
    private companion object {
        val logger = KotlinLogging.logger { };
    }

    @Async(AsyncConfig.MailExecutor.NAME)
    override fun sendEmailAsync(request: EmailSendRequest) {
        logger.info { "Mock send mail to: ${request.to}, subject: ${request.subject}" }
    }
}