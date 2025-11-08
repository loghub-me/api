package me.loghub.api.worker

import me.loghub.api.constant.redis.RedisKeys
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.topic.TopicRepository
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
    private val topicRepository: TopicRepository,
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
            RedisKeys.Article.TRENDING_SCORE().key,
            articleRepository::clearTrendingScore,
            articleRepository::updateTrendingScoreById,
        )
        updateTrendingScore(
            RedisKeys.Series.TRENDING_SCORE().key,
            seriesRepository::clearTrendingScore,
            seriesRepository::updateTrendingScoreById
        )
        updateTrendingScore(
            RedisKeys.Question.TRENDING_SCORE().key,
            questionRepository::clearTrendingScore,
            questionRepository::updateTrendingScoreById
        )

        topicRepository.updateTrendingScoresFromTrendingTopic()
        topicRepository.clearTrendingScoresNotInTrendingTopic()
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