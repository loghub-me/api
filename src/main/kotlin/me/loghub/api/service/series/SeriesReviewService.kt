package me.loghub.api.service.series

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.series.review.PostSeriesReviewDTO
import me.loghub.api.dto.series.review.SeriesReviewDTO
import me.loghub.api.entity.series.SeriesReview
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.series.SeriesReviewMapper
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.series.SeriesReviewRepository
import me.loghub.api.util.checkConflict
import me.loghub.api.util.checkPermission
import me.loghub.api.util.orElseThrowNotFound
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SeriesReviewService(
    private val seriesRepository: SeriesRepository,
    private val seriesReviewRepository: SeriesReviewRepository,
) {
    private companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }

    @Transactional(readOnly = true)
    fun getReviews(seriesId: Long, page: Int): Page<SeriesReviewDTO> =
        seriesReviewRepository.findAllBySeriesId(
            seriesId = seriesId,
            pageable = PageRequest.of(
                page - 1,
                DEFAULT_PAGE_SIZE,
                Sort.by(SeriesReview::createdAt.name).descending()
            ),
        ).map(SeriesReviewMapper::map)

    @Transactional
    fun postReview(seriesId: Long, requestBody: PostSeriesReviewDTO, writer: User): SeriesReview {
        val series = seriesRepository.findById(seriesId)
            .orElseThrowNotFound { ResponseMessage.Series.NOT_FOUND }

        checkConflict(seriesReviewRepository.existsBySeriesAndWriter(series, writer)) {
            ResponseMessage.Series.Review.ALREADY_EXISTS
        }

        val comment = requestBody.toEntity(series, writer)
        series.incrementReviewCount()
        return seriesReviewRepository.save(comment)
    }

    @Transactional
    fun deleteReview(seriesId: Long, commentId: Long, writer: User) {
        val comment = seriesReviewRepository.findWithGraphBySeriesIdAndId(seriesId, commentId)
            ?: throw EntityNotFoundException(ResponseMessage.Series.Review.NOT_FOUND)

        checkPermission(comment.writer == writer) { ResponseMessage.Series.Review.PERMISSION_DENIED }

        comment.series.decrementReviewCount()
        seriesReviewRepository.delete(comment)
    }
}