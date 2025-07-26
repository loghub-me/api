package kr.loghub.api.service.series

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.series.review.PostSeriesReviewDTO
import kr.loghub.api.dto.series.review.SeriesReviewDTO
import kr.loghub.api.entity.series.SeriesReview
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.mapper.series.SeriesReviewMapper
import kr.loghub.api.repository.series.SeriesRepository
import kr.loghub.api.repository.series.SeriesReviewRepository
import kr.loghub.api.util.checkAlreadyExists
import kr.loghub.api.util.checkPermission
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
            .orElseThrow { EntityNotFoundException(ResponseMessage.Series.NOT_FOUND) }

        checkAlreadyExists(seriesReviewRepository.existsBySeriesAndWriter(series, writer)) {
            ResponseMessage.Series.Review.ALREADY_EXISTS
        }

        val comment = requestBody.toEntity(series, writer)
        series.incrementReviewCount()
        return seriesReviewRepository.save(comment)
    }

    @Transactional
    fun removeReview(seriesId: Long, commentId: Long, writer: User) {
        val comment = seriesReviewRepository.findWithGraphBySeriesIdAndId(seriesId, commentId)
            ?: throw EntityNotFoundException(ResponseMessage.Series.Review.NOT_FOUND)

        checkPermission(comment.writer == writer) { ResponseMessage.Series.Review.PERMISSION_DENIED }

        comment.series.decrementReviewCount()
        seriesReviewRepository.delete(comment)
    }
}