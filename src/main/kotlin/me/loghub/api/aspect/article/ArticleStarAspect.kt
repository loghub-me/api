package me.loghub.api.aspect.article

import me.loghub.api.lib.redis.key.article.ArticleTrendingScoreRedisKey
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Component

@Aspect
@Component
class ArticleStarAspect(private val redisTemplate: RedisTemplate<String, String>) {
    private object TrendingScoreDelta {
        const val STAR = 3.toDouble()
    }

    private val trendingScoreKey = ArticleTrendingScoreRedisKey()
    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleStarService.addStar(..)) && args(articleId, ..)")
    fun afterAddStar(articleId: Long) {
        updateTrendingScoreAfterAddStar(articleId)
    }

    @AfterReturning("execution(* me.loghub.api.service.article.ArticleStarService.deleteStar(..)) && args(articleId, ..)")
    fun afterDeleteStar(articleId: Long) {
        updateTrendingScoreAfterDeleteStar(articleId)
    }

    private fun updateTrendingScoreAfterAddStar(articleId: Long) =
        zSetOps.incrementScore(trendingScoreKey, articleId.toString(), TrendingScoreDelta.STAR)

    private fun updateTrendingScoreAfterDeleteStar(articleId: Long) =
        zSetOps.incrementScore(trendingScoreKey, articleId.toString(), -TrendingScoreDelta.STAR)
}