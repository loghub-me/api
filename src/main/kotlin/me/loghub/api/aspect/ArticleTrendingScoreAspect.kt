package me.loghub.api.aspect

import me.loghub.api.constant.redis.RedisKey
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class ArticleTrendingScoreAspect(private val redisTemplate: RedisTemplate<String, String>) {
    private object TrendingScoreDelta {
        const val COMMENT = 1.toDouble()
        const val STAR = 3.toDouble()
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleCommentService.postComment(..)) && args(articleId, ..))")
    fun updateTrendingScoreAfterPostArticleComment(articleId: Long) =
        zSetOps.incrementScore(RedisKey.Article.TRENDING_SCORE, articleId.toString(), TrendingScoreDelta.COMMENT)

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleCommentService.removeComment(..)) && args(articleId, ..))")
    fun updateTrendingScoreAfterRemoveArticleComment(articleId: Long) =
        zSetOps.incrementScore(RedisKey.Article.TRENDING_SCORE, articleId.toString(), -TrendingScoreDelta.COMMENT)

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleStarService.addStar(..)) && args(articleId, ..)")
    fun updateTrendingScoreAfterAddArticleStar(articleId: Long) =
        zSetOps.incrementScore(RedisKey.Article.TRENDING_SCORE, articleId.toString(), TrendingScoreDelta.STAR)

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleStarService.removeStar(..)) && args(articleId, ..)")
    fun updateTrendingScoreAfterRemoveArticleStar(articleId: Long) =
        zSetOps.incrementScore(RedisKey.Article.TRENDING_SCORE, articleId.toString(), -TrendingScoreDelta.STAR)
}