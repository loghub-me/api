package me.loghub.api.service.internal

import me.loghub.api.dto.config.AssetsProperties
import me.loghub.api.dto.config.ClientProperties
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
    private val clientProps: ClientProperties,
    private val assetsProps: AssetsProperties,
) {
    private companion object {
        const val ARTICLE_PRIORITY = 1.0
        const val SERIES_PRIORITY = 0.8
        const val SERIES_CHAPTER_PRIORITY = 1.0
        const val QUESTION_PRIORITY = 0.8
        const val TOPIC_PRIORITY = 0.6
    }

    @Transactional(readOnly = true)
    fun getDynamicSitemap() = (
            articleRepository.findSitemap(
                clientProps.host,
                assetsProps.host,
                ARTICLE_PRIORITY
            ) + seriesRepository.findSitemap(
                clientProps.host,
                assetsProps.host,
                SERIES_PRIORITY,
                SERIES_CHAPTER_PRIORITY
            ) + questionRepository.findSitemap(
                clientProps.host,
                assetsProps.host,
                QUESTION_PRIORITY
            ) + topicRepository.findSitemap(
                clientProps.host,
                assetsProps.host,
                TOPIC_PRIORITY
            ))
}