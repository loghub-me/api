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
        if (postedArticle.published) {
            addUserActivityAfterPublishArticle(postedArticle)
        }
        logAfterPostArticle(postedArticle)
    }

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleService.editArticle(..))",
        returning = "editedArticle"
    )
    fun afterEditArticle(editedArticle: Article) {
        if (editedArticle.published) {
            addUserActivityAfterPublishArticle(editedArticle)
        } else {
            removeUserActivityAfterUnpublishArticle(editedArticle)
        }
        logAfterEditArticle(editedArticle)
    }

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleService.deleteArticle(..)) && args(articleId, writer))")
    fun afterDeleteArticle(articleId: Long, writer: User) {
        logAfterDeleteArticle(articleId, writer)
    }

    private fun addUserActivityAfterPublishArticle(postedArticle: Article) {
        postedArticle.publishedAt?.let { publishedAt ->
            userActivityRepository.save(
                UserActivity(
                    action = UserActivity.Action.PUBLISH_ARTICLE,
                    createdAt = publishedAt,
                    createdDate = publishedAt.toLocalDate(),
                    user = postedArticle.writer,
                    article = postedArticle
                )
            )
        }
    }

    private fun removeUserActivityAfterUnpublishArticle(editedArticle: Article) =
        userActivityRepository.deleteByArticle(editedArticle)

    private fun logAfterPostArticle(article: Article) =
        logger.info { "[Article] posted: { articleId=${article.id}, writerId=${article.writer.id}, title=\"${article.title}\", published=${article.published} }" }

    private fun logAfterEditArticle(article: Article) =
        logger.info { "[Article] edited: { articleId=${article.id}, writerId=${article.writer.id}, title=\"${article.title}\", published=${article.published} }" }

    private fun logAfterDeleteArticle(articleId: Long, writer: User) =
        logger.info { "[Article] deleted: { articleId=${articleId}, writerId=${writer.id} }" }
}