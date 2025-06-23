package kr.loghub.api.dto.internal.mail

interface MailDTO {
    val to: String;
    val subject: String;
    val html: String;
}