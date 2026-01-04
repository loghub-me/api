package me.loghub.api.service.internal

import me.loghub.api.config.AssetsConfig
import me.loghub.api.config.ClientConfig
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.topic.TopicRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SitemapService(
    private val articleRepository: ArticleRepository,
    private val seriesRepository: SeriesRepository,
    private val questionRepository: QuestionRepository,
    private val topicRepository: TopicRepository,
) {
    private companion object {
        const val ARTICLE_PRIORITY = 1.0
        const val SERIES_PRIORITY = 1.0
        const val SERIES_CHAPTER_PRIORITY = 1.0
        const val QUESTION_PRIORITY = 0.7
        const val TOPIC_PRIORITY = 0.6
    }

    @Transactional(readOnly = true)
    fun getDynamicSitemap() = (
            articleRepository.findSitemap(
                ClientConfig.HOST,
                AssetsConfig.HOST,
                ARTICLE_PRIORITY
            ) + seriesRepository.findSitemap(
                ClientConfig.HOST,
                AssetsConfig.HOST,
                SERIES_PRIORITY,
                SERIES_CHAPTER_PRIORITY
            ) + questionRepository.findSitemap(
                ClientConfig.HOST,
                AssetsConfig.HOST,
                QUESTION_PRIORITY
            ) + topicRepository.findSitemap(
                ClientConfig.HOST,
                AssetsConfig.HOST,
                TOPIC_PRIORITY
            ))
}