package me.loghub.api.aspect.article

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.article.ArticleComment
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class ArticleCommentAspect {
    private companion object {
        val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleCommentService.postComment(..))",
        returning = "postedComment"
    )
    fun afterPostComment(postedComment: ArticleComment) {
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
        logAfterDeleteComment(articleId, commentId, writer)
    }

    private fun logAfterPostComment(comment: ArticleComment) =
        logger.info { "[ArticleComment] posted: { articleId=${comment.article.id}, commentId=${comment.id}, writerId=${comment.writer.id}, content=\"${comment.content}\" }" }

    private fun logAfterEditComment(comment: ArticleComment) =
        logger.info { "[ArticleComment] edited: { articleId=${comment.article.id}, commentId=${comment.id}, writerId=${comment.writer.id}, content=\"${comment.content}\" }" }

    private fun logAfterDeleteComment(articleId: Long, commentId: Long, writer: User) =
        logger.info { "[ArticleComment] deleted: { articleId=${articleId}, commentId=${commentId}, writerId=${writer.id} }" }
}
