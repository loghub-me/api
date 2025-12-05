package me.loghub.api.worker

import me.loghub.api.lib.redis.key.RedisKeys
import me.loghub.api.repository.article.ArticleTrendingScoreRepository
import me.loghub.api.repository.question.QuestionTrendingScoreRepository
import me.loghub.api.repository.series.SeriesTrendingScoreRepository
import me.loghub.api.repository.topic.TopicRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TrendingScoreWorker(
    private val articleTrendingScoreRepository: ArticleTrendingScoreRepository,
    private val seriesTrendingScoreRepository: SeriesTrendingScoreRepository,
    private val questionTrendingScoreRepository: QuestionTrendingScoreRepository,
    private val redisTemplate: RedisTemplate<String, String>,
    private val topicRepository: TopicRepository,
) {
    private companion object {
        const val CRON = "0 0 */6 * * *" // Every 6 hours
        const val MAX_SIZE = 49L
        const val DECAY_FACTOR = 0.85
        const val LOW_SCORE_THRESHOLD = 0.1
    }

    private val zSetOps: ZSetOperations<String, String>
        get() = redisTemplate.opsForZSet()

    @Scheduled(cron = CRON)
    @Transactional
    fun updateTrendingScores() {
        updateTrendingScore(
            RedisKeys.Article.TRENDING_SCORE().key,
            articleTrendingScoreRepository::decayTrendingScores,
            articleTrendingScoreRepository::clearLowTrendingScores,
            articleTrendingScoreRepository::incrementTrendingScoreById,
        )
        updateTrendingScore(
            RedisKeys.Series.TRENDING_SCORE().key,
            seriesTrendingScoreRepository::decayTrendingScores,
            seriesTrendingScoreRepository::clearLowTrendingScores,
            seriesTrendingScoreRepository::incrementTrendingScoreById
        )
        updateTrendingScore(
            RedisKeys.Question.TRENDING_SCORE().key,
            questionTrendingScoreRepository::decayTrendingScores,
            questionTrendingScoreRepository::clearLowTrendingScores,
            questionTrendingScoreRepository::incrementTrendingScoreById
        )

        topicRepository.updateTrendingScoresFromTrendingTopic()
        topicRepository.clearTrendingScoresNotInTrendingTopic()
    }

    private fun updateTrendingScore(
        key: String,
        decay: (factor: Double) -> Int,
        clear: (threshold: Double) -> Int,
        increment: (Double, Long) -> Int
    ) {
        decay(DECAY_FACTOR)
        clear(LOW_SCORE_THRESHOLD)

        if (!redisTemplate.hasKey(key)) {
            return
        }

        val tempKey = "${key}_temp"
        redisTemplate.rename(key, tempKey)  // to avoid race condition
        zSetOps.reverseRangeWithScores(tempKey, 0, MAX_SIZE)?.forEach { entry ->
            val articleId = entry.value?.toLong() ?: return@forEach
            val score = entry.score ?: 0.0
            increment(score, articleId)
        }

        redisTemplate.delete(tempKey)
    }
}