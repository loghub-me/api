package me.loghub.api.dto.task.email

interface EmailSendRequest {
    val to: String;
    val subject: String;
    val html: String;
}