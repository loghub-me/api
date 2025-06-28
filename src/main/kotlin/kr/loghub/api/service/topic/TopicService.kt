package kr.loghub.api.service.topic

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.topic.TopicDTO
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.mapper.topic.TopicMapper
import kr.loghub.api.repository.topic.TopicRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TopicService(private val topicRepository: TopicRepository) {
    @Transactional(readOnly = true)
    fun getTrendingArticles(query: String): List<TopicDTO> {
        val topics = if (query.isBlank())
            topicRepository.findTop20ByOrderByTrendingScoreDesc() else
            topicRepository.findByNameContainingIgnoreCaseOrderByTrendingScoreDesc(query)
        return topics.map { TopicMapper.map(it) }
    }

    @Transactional(readOnly = true)
    fun getTopic(slug: String) = topicRepository.findBySlug(slug)
        ?.let { TopicMapper.mapDetail(it) }
        ?: throw EntityNotFoundException(ResponseMessage.Topic.NOT_FOUND)
}