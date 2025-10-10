package me.loghub.api.aspect.logging.article

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.user.User
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class ArticleLoggingAspect {
    private companion object {
        private val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleService.postArticle(..)) && args(.., writer)",
        returning = "article"
    )
    fun afterPostArticle(writer: User, article: Article) =
        logger.info { "[Article] posted: { articleId=${article.id}, writerId=${writer.id}, title=\"${article.title}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleService.editArticle(..)) && args(.., writer)",
        returning = "article"
    )
    fun afterEditArticle(writer: User, article: Article) =
        logger.info { "[Article] edited: { articleId=${article.id}, writerId=${writer.id}, title=\"${article.title}\" }" }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleService.deleteArticle(..)) && args(articleId, writer)"
    )
    fun afterDeleteArticle(articleId: Long, writer: User) =
        logger.info { "[Article] deleted: { articleId=${articleId}, writerId=${writer.id} }" }
}