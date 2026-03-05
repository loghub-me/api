package me.loghub.api.service.email

import me.loghub.api.dto.task.email.EmailSendRequest

interface EmailService {
    fun sendEmailAsync(request: EmailSendRequest)
}