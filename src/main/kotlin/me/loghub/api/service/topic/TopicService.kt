package me.loghub.api.service.topic

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.article.ArticleDTO
import me.loghub.api.dto.question.QuestionDTO
import me.loghub.api.dto.series.SeriesDTO
import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.article.ArticleMapper
import me.loghub.api.mapper.question.QuestionMapper
import me.loghub.api.mapper.series.SeriesMapper
import me.loghub.api.mapper.topic.TopicMapper
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.topic.TopicRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TopicService(
    private val topicRepository: TopicRepository,
    private val articleRepository: ArticleRepository,
    private val seriesRepository: SeriesRepository,
    private val questionRepository: QuestionRepository,
) {
    @Transactional(readOnly = true)
    @Cacheable("getTrendingTopics")
    fun getTrendingTopics(): List<TopicDTO> =
        topicRepository.findAllByOrderByTrendingScoreDesc()
            .map { TopicMapper.map(it) }

    @Transactional(readOnly = true)
    fun getTopic(slug: String) = topicRepository.findBySlug(slug)
        ?.let { TopicMapper.mapDetail(it) }
        ?: throw EntityNotFoundException(ResponseMessage.Topic.NOT_FOUND)

    @Transactional(readOnly = true)
    fun getTrendingArticles(slug: String): List<ArticleDTO> =
        articleRepository.findTop10ByTopicIdOrderByTrendingScoreDesc(slug)
            .map { ArticleMapper.map(it) }

    @Transactional(readOnly = true)
    fun getTrendingSeries(slug: String): List<SeriesDTO> =
        seriesRepository.findTop10ByTopicIdOrderByTrendingScoreDesc(slug)
            .map { SeriesMapper.map(it) }

    @Transactional(readOnly = true)
    fun getTrendingQuestions(slug: String): List<QuestionDTO> =
        questionRepository.findTop10ByTopicIdOrderByTrendingScoreDesc(slug)
            .map { QuestionMapper.map(it) }
}