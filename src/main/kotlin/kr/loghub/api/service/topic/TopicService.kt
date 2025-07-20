package kr.loghub.api.service.topic

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.article.ArticleDTO
import kr.loghub.api.dto.question.QuestionDTO
import kr.loghub.api.dto.series.SeriesDTO
import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.mapper.article.ArticleMapper
import kr.loghub.api.mapper.question.QuestionMapper
import kr.loghub.api.mapper.series.SeriesMapper
import kr.loghub.api.mapper.topic.TopicMapper
import kr.loghub.api.repository.article.ArticleRepository
import kr.loghub.api.repository.question.QuestionRepository
import kr.loghub.api.repository.series.SeriesRepository
import kr.loghub.api.repository.topic.TopicRepository
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
    fun getTrendingTopics(): List<TopicDTO> =
        topicRepository.findTop20ByOrderByTrendingScoreDesc()
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