package kr.loghub.api.worker

import com.resend.Resend
import com.resend.core.exception.ResendException
import com.resend.services.emails.model.CreateEmailOptions
import kr.loghub.api.dto.internal.mail.MailDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class MailSendWorker(
    private val resend: Resend,
    @Value("\${resend.from}") private val from: String,
) {
    companion object {
        private val queue = ConcurrentLinkedQueue<MailDTO>()
    }

    @Scheduled(fixedRate = 1000 * 10)  // 10 seconds
    fun process() {
        while (queue.isNotEmpty()) {
            val dto = queue.poll() ?: break
            try {
                sendMail(dto)
            } catch (_: ResendException) {
                continue
            }
        }
    }

    fun addToQueue(dto: MailDTO) = queue.add(dto)

    private fun sendMail(dto: MailDTO) {
        val email = CreateEmailOptions.builder()
            .from(from).to(dto.to)
            .subject(dto.subject).html(dto.html)
            .build()
//        resend.emails().send(email)
    }
}