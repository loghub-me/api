package me.loghub.api.aspect.article

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.article.ArticleComment
import me.loghub.api.entity.user.User
import me.loghub.api.lib.redis.key.article.ArticleTrendingScoreRedisKey
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class ArticleCommentAspect(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private object TrendingScoreDelta {
        const val COMMENT = 1.toDouble()
    }

    private companion object {
        val logger = KotlinLogging.logger { };
    }

    private val trendingScoreKey = ArticleTrendingScoreRedisKey()
    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleCommentService.postComment(..))",
        returning = "postedComment"
    )
    fun afterPostComment(postedComment: ArticleComment) {
        val articleId = postedComment.article.id!!
        updateTrendingScoreAfterPostComment(articleId)
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
        zSetOps.incrementScore(trendingScoreKey, articleId.toString(), TrendingScoreDelta.COMMENT)

    private fun updateTrendingScoreAfterDeleteComment(articleId: Long) =
        zSetOps.incrementScore(trendingScoreKey, articleId.toString(), -TrendingScoreDelta.COMMENT)

    private fun logAfterPostComment(comment: ArticleComment) =
        logger.info { "[ArticleComment] posted: { articleId=${comment.article.id}, commentId=${comment.id}, writerId=${comment.writer.id}, content=\"${comment.content}\" }" }

    private fun logAfterEditComment(comment: ArticleComment) =
        logger.info { "[ArticleComment] edited: { articleId=${comment.article.id}, commentId=${comment.id}, writerId=${comment.writer.id}, content=\"${comment.content}\" }" }

    private fun logAfterDeleteComment(articleId: Long, commentId: Long, writer: User) =
        logger.info { "[ArticleComment] deleted: { articleId=${articleId}, commentId=${commentId}, writerId=${writer.id} }" }
}
