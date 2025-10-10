package me.loghub.api.aspect.logging.article

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.article.ArticleComment
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class ArticleCommentLoggingAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleCommentService.postComment(..)) && args(articleId, .., writer)",
        returning = "comment"
    )
    fun afterPostComment(articleId: Long, writer: User, comment: ArticleComment) =
        logger.info { "[ArticleComment] posted: { articleId=${articleId}, commentId=${comment.id}, writerId=${writer.id}, content=\"${comment.content}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleCommentService.deleteComment(..)) && args(articleId, commentId, writer)"
    )
    fun afterDeleteComment(articleId: Long, commentId: Long, writer: User) =
        logger.info { "[ArticleComment] deleted: { articleId=${articleId}, commentId=${commentId}, writerId=${writer.id} }" }
}