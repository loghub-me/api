package me.loghub.api.mapper.notification

import me.loghub.api.dto.notification.NotificationDTO
import me.loghub.api.entity.notification.Notification
import me.loghub.api.mapper.user.UserMapper
import java.time.format.DateTimeFormatter

object NotificationMapper {
    fun map(notification: Notification) = NotificationDTO(
        id = notification.id!!,
        href = mapHref(notification),
        title = when (notification.targetType) {
            Notification.TargetType.ARTICLE, Notification.TargetType.ARTICLE_COMMENT -> notification.article?.title
                ?: "알 수 없는 게시글"

            Notification.TargetType.SERIES, Notification.TargetType.SERIES_REVIEW -> notification.series
                ?.title ?: "알 수 없는 시리즈"

            Notification.TargetType.QUESTION, Notification.TargetType.QUESTION_ANSWER -> notification.question
                ?.title ?: "알 수 없는 질문"
        },
        message = "새로운 ${notification.targetType.targetName}을(를) 등록했습니다.",
        read = notification.read,
        createdAt = notification.createdAt.format(DateTimeFormatter.ISO_DATE_TIME),
        actor = UserMapper.map(notification.actor),
    )

    fun mapHref(notification: Notification) = when (notification.targetType) {
        Notification.TargetType.ARTICLE -> notification.article?.let { "/articles/${it.writerUsername}/${it.slug}" }
            ?: ""

        Notification.TargetType.ARTICLE_COMMENT -> notification.article?.let { "/articles/${it.writerUsername}/${it.slug}#comments" }
            ?: ""

        Notification.TargetType.SERIES -> notification.series?.let { "/series/${it.writerUsername}/${it.slug}" }
            ?: ""

        Notification.TargetType.SERIES_REVIEW -> notification.series?.let { "/series/${it.writerUsername}/${it.slug}#reviews" }
            ?: ""

        Notification.TargetType.QUESTION -> notification.question?.let { "/questions/${it.writerUsername}/${it.slug}" }
            ?: ""

        Notification.TargetType.QUESTION_ANSWER -> notification.question?.let { "/questions/${it.writerUsername}/${it.slug}#answers" }
            ?: ""
    }
}
