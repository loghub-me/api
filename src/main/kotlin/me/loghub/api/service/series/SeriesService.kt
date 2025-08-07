package me.loghub.api.service.series

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.series.PostSeriesDTO
import me.loghub.api.dto.series.SeriesDTO
import me.loghub.api.dto.series.SeriesDetailDTO
import me.loghub.api.dto.series.SeriesSort
import me.loghub.api.entity.series.Series
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.series.SeriesMapper
import me.loghub.api.repository.series.SeriesCustomRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.topic.TopicRepository
import me.loghub.api.util.checkField
import me.loghub.api.util.checkPermission
import me.loghub.api.util.toSlug
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class SeriesService(
    private val seriesRepository: SeriesRepository,
    private val seriesCustomRepository: SeriesCustomRepository,
    private val topicRepository: TopicRepository,
) {
    private companion object {
        private const val PAGE_SIZE = 20
    }

    @Transactional(readOnly = true)
    fun searchSeries(query: String, sort: SeriesSort, page: Int): Page<SeriesDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        return seriesCustomRepository.search(
            query = query.trim(),
            sort = sort,
            pageable = PageRequest.of(page - 1, PAGE_SIZE)
        ).map(SeriesMapper::map)
    }

    @Transactional(readOnly = true)
    fun getSeries(username: String, slug: String): SeriesDetailDTO =
        seriesRepository.findWithGraphByCompositeKey(username, slug)
            ?.let { SeriesMapper.mapDetail(it) }
            ?: throw EntityNotFoundException(ResponseMessage.Series.NOT_FOUND)

    @Transactional
    fun postSeries(requestBody: PostSeriesDTO, writer: User): Series {
        val slug = generateUniqueSlug(writer.username, requestBody.title)
        val topics = topicRepository.findBySlugIn(requestBody.topicSlugs)

        val series = requestBody.toEntity(slug, writer, topics)
        return seriesRepository.save(series)
    }

    @Transactional
    fun editSeries(seriesId: Long, requestBody: PostSeriesDTO, writer: User): Series {
        val series = seriesRepository.findWithWriterById(seriesId)
            ?: throw EntityNotFoundException(ResponseMessage.Series.NOT_FOUND)

        checkPermission(series.writer == writer) { ResponseMessage.Series.PERMISSION_DENIED }

        val slug = generateUniqueSlug(writer.username, requestBody.title)
        val topics = topicRepository.findBySlugIn(requestBody.topicSlugs)

        series.update(requestBody)
        series.updateSlug(slug)
        series.updateTopics(topics)
        return series
    }

    @Transactional
    fun removeSeries(seriesId: Long, writer: User) {
        val series = seriesRepository.findWithWriterById(seriesId)
            ?: throw EntityNotFoundException(ResponseMessage.Series.NOT_FOUND)

        checkPermission(series.writer == writer) { ResponseMessage.Series.PERMISSION_DENIED }

        seriesRepository.delete(series)
    }

    private fun generateUniqueSlug(username: String, title: String): String {
        var slug = title.toSlug()
        while (seriesRepository.existsByCompositeKey(username, slug)) {
            slug = "$slug-${UUID.randomUUID()}"
        }
        return slug
    }
}