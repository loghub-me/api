package me.loghub.api.worker

import me.loghub.api.constant.redis.RedisKey
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.series.SeriesRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TrendingScoreWorker(
    private val articleRepository: ArticleRepository,
    private val seriesRepository: SeriesRepository,
    private val questionRepository: QuestionRepository,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private companion object {
        const val CRON = "0 0 */6 * * *" // Every 6 hours
        const val MAX_SIZE = 49L
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @Scheduled(cron = CRON)
    @Transactional
    fun updateTrendingScores() {
        updateTrendingScore(
            RedisKey.Article.TRENDING_SCORE,
            articleRepository::clearTrendingScore,
            articleRepository::updateTrendingScoreById,
        )
        updateTrendingScore(
            RedisKey.Series.TRENDING_SCORE,
            seriesRepository::clearTrendingScore,
            seriesRepository::updateTrendingScoreById
        )
        updateTrendingScore(
            RedisKey.Question.TRENDING_SCORE,
            questionRepository::clearTrendingScore,
            questionRepository::updateTrendingScoreById
        )
    }

    private fun updateTrendingScore(
        trendingScoreKey: String,
        clearTrendingScore: () -> Unit,
        updateTrendingScoreById: (Double, Long) -> Int
    ) {
        clearTrendingScore()

        if (!redisTemplate.hasKey(trendingScoreKey)) {
            return
        }

        val tempKey = "${trendingScoreKey}_temp"
        redisTemplate.rename(trendingScoreKey, tempKey)  // to avoid race condition
        zSetOps.reverseRangeWithScores(tempKey, 0, MAX_SIZE)?.forEach { entry ->
            val articleId = entry.value?.toLong() ?: return@forEach
            val score = entry.score ?: 0.0
            updateTrendingScoreById(score, articleId)
        }

        redisTemplate.delete(tempKey)
    }
}