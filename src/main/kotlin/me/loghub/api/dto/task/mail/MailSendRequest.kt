package me.loghub.api.dto.task.mail

interface MailSendRequest {
    val to: String;
    val subject: String;
    val html: String;
}