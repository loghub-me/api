package me.loghub.api.aspect.article

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.constant.redis.RedisKeys
import me.loghub.api.dto.notification.NotificationDTO
import me.loghub.api.entity.article.ArticleComment
import me.loghub.api.entity.user.User
import me.loghub.api.service.user.NotificationService
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class ArticleCommentAspect(
    private val redisTemplate: RedisTemplate<String, String>,
    private val notificationService: NotificationService,
) {
    private object TrendingScoreDelta {
        const val COMMENT = 1.toDouble()
    }

    private companion object {
        val logger = KotlinLogging.logger { };
    }

    private val trendingScoreKey = RedisKeys.Article.TRENDING_SCORE()
    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleCommentService.postComment(..))",
        returning = "postedComment"
    )
    fun afterPostComment(postedComment: ArticleComment) {
        val articleId = postedComment.article.id!!
        updateTrendingScoreAfterPostComment(articleId)
        sendNotificationsAfterPostComment(postedComment)
        logAfterPostComment(postedComment)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleCommentService.editComment(..))",
        returning = "editedComment"
    )
    fun afterEditComment(editedComment: ArticleComment) {
        logAfterEditComment(editedComment)
    }

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleCommentService.deleteComment(..)) && args(articleId, commentId, writer))")
    fun afterDeleteComment(articleId: Long, commentId: Long, writer: User) {
        updateTrendingScoreAfterDeleteComment(articleId)
        logAfterDeleteComment(articleId, commentId, writer)
    }

    private fun updateTrendingScoreAfterPostComment(articleId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, articleId.toString(), TrendingScoreDelta.COMMENT)

    private fun updateTrendingScoreAfterDeleteComment(articleId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, articleId.toString(), -TrendingScoreDelta.COMMENT)

    private fun sendNotificationsAfterPostComment(postedComment: ArticleComment) {
        val article = postedComment.article

        val href = "/articles/${article.writerUsername}/${article.slug}"
        val title = article.title

        postedComment.parent?.let { parentComment ->
            if (parentComment.writer == postedComment.writer) return
            val message = "@${postedComment.writer.username}님이 회원님의 댓글에 답글을 남겼습니다."
            val notification = NotificationDTO(href, title, message)
            notificationService.addNotification(parentComment.writer.id!!, notification)
        }

        if (article.writer == postedComment.writer) return
        val message = "@${postedComment.writer.username}님이 회원님의 아티클에 댓글을 남겼습니다."
        val notification = NotificationDTO(href, title, message)
        notificationService.addNotification(article.writer.id!!, notification)
    }

    private fun logAfterPostComment(postedComment: ArticleComment) =
        logger.info { "[ArticleComment] posted: { articleId=${postedComment.article.id}, commentId=${postedComment.id}, writerId=${postedComment.writer.id}, content=\"${postedComment.content}\" }" }

    private fun logAfterEditComment(editedComment: ArticleComment) =
        logger.info { "[ArticleComment] edited: { articleId=${editedComment.article.id}, commentId=${editedComment.id}, writerId=${editedComment.writer.id}, content=\"${editedComment.content}\" }" }

    private fun logAfterDeleteComment(articleId: Long, commentId: Long, writer: User) =
        logger.info { "[ArticleComment] deleted: { articleId=${articleId}, commentId=${commentId}, writerId=${writer.id} }" }
}