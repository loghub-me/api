package me.loghub.api.service.topic

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.article.ArticleDTO
import me.loghub.api.dto.question.QuestionDTO
import me.loghub.api.dto.series.SeriesDTO
import me.loghub.api.dto.topic.TopicArticleSort
import me.loghub.api.dto.topic.TopicDTO
import me.loghub.api.dto.topic.TopicQuestionSort
import me.loghub.api.dto.topic.TopicSeriesSort
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.article.ArticleMapper
import me.loghub.api.mapper.question.QuestionMapper
import me.loghub.api.mapper.series.SeriesMapper
import me.loghub.api.mapper.topic.TopicMapper
import me.loghub.api.repository.article.ArticleCustomRepository
import me.loghub.api.repository.question.QuestionCustomRepository
import me.loghub.api.repository.series.SeriesCustomRepository
import me.loghub.api.repository.topic.TopicRepository
import me.loghub.api.util.checkField
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TopicService(
    private val topicRepository: TopicRepository,
    private val articleCustomRepository: ArticleCustomRepository,
    private val seriesCustomRepository: SeriesCustomRepository,
    private val questionCustomRepository: QuestionCustomRepository,
) {
    private companion object {
        private const val ARTICLE_PAGE_SIZE = 20
        private const val SERIES_PAGE_SIZE = 20
        private const val QUESTION_PAGE_SIZE = 20
    }

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
    fun getTopicArticles(slug: String, sort: TopicArticleSort, page: Int): Page<ArticleDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        return articleCustomRepository.findByTopicSlug(
            topicSlug = slug,
            sort = sort,
            pageable = PageRequest.of(page - 1, ARTICLE_PAGE_SIZE)
        ).map { ArticleMapper.map(it) }
    }

    @Transactional(readOnly = true)
    fun getTopicSeries(slug: String, sort: TopicSeriesSort, page: Int): Page<SeriesDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        return seriesCustomRepository.findByTopicSlug(
            topicSlug = slug,
            sort = sort,
            pageable = PageRequest.of(page - 1, ARTICLE_PAGE_SIZE)
        ).map { SeriesMapper.map(it) }
    }

    @Transactional(readOnly = true)
    fun getTopicQuestions(slug: String, sort: TopicQuestionSort, page: Int): Page<QuestionDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        return questionCustomRepository.findByTopicSlug(
            topicSlug = slug,
            sort = sort,
            pageable = PageRequest.of(page - 1, ARTICLE_PAGE_SIZE)
        ).map { QuestionMapper.map(it) }
    }
}