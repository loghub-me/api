package me.loghub.api.dto.notification

import me.loghub.api.entity.article.Article
import me.loghub.api.entity.notification.Notification
import me.loghub.api.entity.question.Question
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.user.User

data class CreateNotificationDTO(
    val type: Notification.Type = Notification.Type.INFO,
    val targetType: Notification.TargetType,
    val article: Article? = null,
    val series: Series? = null,
    val question: Question? = null,
    val actor: User,
    val recipient: User,
) {
    fun toEntity() = Notification(
        type = type,
        targetType = targetType,
        article = article,
        series = series,
        question = question,
        actor = actor,
        recipient = recipient,
    )
}
