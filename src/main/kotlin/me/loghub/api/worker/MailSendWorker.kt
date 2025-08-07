package me.loghub.api.worker

import com.resend.Resend
import com.resend.core.exception.ResendException
import com.resend.services.emails.model.CreateEmailOptions
import me.loghub.api.dto.task.mail.MailSendRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class MailSendWorker(
    private val resendClient: Resend,
    @Value("\${resend.from}") private val from: String,
) {
    private companion object {
        private val queue = ConcurrentLinkedQueue<MailSendRequest>()
        private const val CRON = "0/10 * * * * *" // Every 10 seconds
    }

    @Scheduled(cron = CRON)
    fun process() {
        while (queue.isNotEmpty()) {
            val request = queue.poll() ?: break
            sendMail(request)
        }
    }

    fun addToQueue(request: MailSendRequest) = queue.add(request)

    private fun sendMail(request: MailSendRequest) {
        val params = CreateEmailOptions.builder()
            .from(from)
            .to(request.to)
            .subject(request.subject)
            .html(request.html)
            .build()

        try {
            resendClient.emails().send(params)
        } catch (_: ResendException) {
            return
        }
    }
}