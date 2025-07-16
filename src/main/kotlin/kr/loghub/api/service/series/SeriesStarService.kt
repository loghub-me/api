package kr.loghub.api.service.series

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.entity.star.Star
import kr.loghub.api.entity.user.User
import kr.loghub.api.exception.entity.EntityNotFoundException
import kr.loghub.api.repository.series.SeriesRepository
import kr.loghub.api.repository.star.StarRepository
import kr.loghub.api.service.star.StarService
import kr.loghub.api.util.checkAlreadyExists
import kr.loghub.api.util.checkExists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SeriesStarService(
    private val starRepository: StarRepository,
    private val seriesRepository: SeriesRepository,
) : StarService {
    @Transactional(readOnly = true)
    override fun existsStar(seriesId: Long, user: User): Boolean =
        starRepository.existsBySeriesIdAndUser(seriesId, user)

    @Transactional
    override fun addStar(seriesId: Long, user: User): Star {
        val series = seriesRepository.findById(seriesId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Series.NOT_FOUND) }

        checkAlreadyExists(starRepository.existsBySeriesAndUser(series, user)) {
            ResponseMessage.Star.ALREADY_EXISTS
        }

        series.incrementStarCount()
        return starRepository.save(Star(user = user, series = series, target = Star.Target.SERIES))
    }

    @Transactional
    override fun removeStar(seriesId: Long, user: User) {
        val affectedRows = starRepository.deleteBySeriesIdAndUser(seriesId, user)

        checkExists(affectedRows > 0) { ResponseMessage.Star.NOT_FOUND }

        seriesRepository.decrementStarCount(seriesId)
    }
}