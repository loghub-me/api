package kr.loghub.api.scheduler

import kr.loghub.api.constant.redis.RedisKey
import kr.loghub.api.repository.article.ArticleRepository
import kr.loghub.api.repository.question.QuestionRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TrendingScoreScheduler(
    private val articleRepository: ArticleRepository,
    private val questionRepository: QuestionRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private companion object {
        const val CRON = "0 0 */6 * * *"
        const val MAX_SIZE = 49L
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @Scheduled(cron = CRON)
    @Transactional
    fun updateTrendingScores() {
        updateTrendingScore(
            RedisKey.Article.TRENDING_SCORE,
            articleRepository::updateTrendingScoreById
        )
        updateTrendingScore(
            RedisKey.Question.TRENDING_SCORE,
            questionRepository::updateTrendingScoreById
        )
    }

    private fun updateTrendingScore(trendingScoreKey: String, updateTrendingScoreById: (Double, Long) -> Int) {
        val tempKey = "${trendingScoreKey}_temp"
        redisTemplate.rename(trendingScoreKey, tempKey)  // to avoid race condition
        zSetOps.reverseRangeWithScores(trendingScoreKey, 0, MAX_SIZE)?.forEach { entry ->
            val articleId = entry.value?.toLong() ?: return@forEach
            val score = entry.score ?: 0.0
            updateTrendingScoreById(score, articleId)
        }
        redisTemplate.delete(trendingScoreKey)
    }
}