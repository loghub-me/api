package me.loghub.api.aspect.article

import io.github.oshai.kotlinlogging.KotlinLogging
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import me.loghub.api.repository.user.UserActivityRepository
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class ArticleAspect(
    private val userActivityRepository: UserActivityRepository,
) {
    private companion object {
        val logger = KotlinLogging.logger { };
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleService.postArticle(..))",
        returning = "postedArticle"
    )
    fun afterPostArticle(postedArticle: Article) {
        addUserActivityAfterPostArticle(postedArticle)
        logAfterPostArticle(postedArticle)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleService.editArticle(..))",
        returning = "editedArticle"
    )
    fun afterEditArticle(editedArticle: Article) {
        logAfterEditArticle(editedArticle)
    }

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleService.deleteArticle(..)) && args(articleId, writer))")
    fun afterDeleteArticle(articleId: Long, writer: User) {
        logAfterDeleteArticle(articleId, writer)
    }

    private fun addUserActivityAfterPostArticle(postedArticle: Article) {
        val activity = UserActivity(
            action = UserActivity.Action.POST_ARTICLE,
            user = postedArticle.writer,
            article = postedArticle
        )
        userActivityRepository.save(activity)
    }

    private fun logAfterPostArticle(postedArticle: Article) =
        logger.info { "[Article] posted: { articleId=${postedArticle.id}, writerId=${postedArticle.writer.id}, title=\"${postedArticle.title}\" }" }

    private fun logAfterEditArticle(editedArticle: Article) =
        logger.info { "[Article] edited: { articleId=${editedArticle.id}, writerId=${editedArticle.writer.id}, title=\"${editedArticle.title}\" }" }

    private fun logAfterDeleteArticle(articleId: Long, writer: User) =
        logger.info { "[Article] deleted: { articleId=${articleId}, writerId=${writer.id} }" }
}