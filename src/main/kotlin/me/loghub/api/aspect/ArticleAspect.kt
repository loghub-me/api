package me.loghub.api.aspect

import me.loghub.api.constant.redis.RedisKeys
import me.loghub.api.entity.article.Article
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserActivity
import me.loghub.api.repository.user.UserActivityRepository
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class ArticleAspect(
    private val userActivityRepository: UserActivityRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private object TrendingScoreDelta {
        const val COMMENT = 1.toDouble()
        const val STAR = 3.toDouble()
    }

    private val trendingScoreKey = RedisKeys.Article.TRENDING_SCORE()
    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning(
        pointcut = "execution(* me.loghub.api.service.article.ArticleService.postArticle(..)) && args(.., writer)",
        returning = "article"
    )
    fun afterPostArticle(writer: User, article: Article) {
        val activity = UserActivity(
            action = UserActivity.Action.POST_ARTICLE,
            user = writer,
            article = article
        )
        userActivityRepository.save(activity)
    }

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleCommentService.postComment(..)) && args(articleId, ..)")
    fun afterPostComment(articleId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, articleId.toString(), TrendingScoreDelta.COMMENT)

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleCommentService.deleteComment(..)) && args(articleId, ..)")
    fun afterDeleteComment(articleId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, articleId.toString(), -TrendingScoreDelta.COMMENT)

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleStarService.addStar(..)) && args(articleId, ..)")
    fun afterAddStar(articleId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, articleId.toString(), TrendingScoreDelta.STAR)

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleStarService.deleteStar(..)) && args(articleId, ..)")
    fun afterDeleteStar(articleId: Long) =
        zSetOps.incrementScore(trendingScoreKey.key, articleId.toString(), -TrendingScoreDelta.STAR)
}