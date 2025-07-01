package kr.loghub.api.worker

import kr.loghub.api.dto.task.mail.MailSendRequest
import kr.loghub.api.proxy.TaskAPIProxy
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentLinkedQueue

@Component
class MailSendWorker(private val taskAPIProxy: TaskAPIProxy) {
    companion object {
        private val queue = ConcurrentLinkedQueue<MailSendRequest>()
    }

    @Scheduled(fixedRate = 1000 * 10)  // 10 seconds
    fun process() {
        while (queue.isNotEmpty()) {
            val request = queue.poll() ?: break
            sendMail(request)
        }
    }

    fun addToQueue(request: MailSendRequest) = queue.add(request)

    private fun sendMail(request: MailSendRequest) = taskAPIProxy.sendMail(request)
}