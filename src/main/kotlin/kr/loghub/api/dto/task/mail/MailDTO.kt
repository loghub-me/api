package kr.loghub.api.dto.task.mail

interface MailDTO {
    val to: String;
    val subject: String;
    val html: String;
}