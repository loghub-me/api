package me.loghub.api.service.series

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserStar
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.user.UserStarRepository
import me.loghub.api.service.common.IStarService
import me.loghub.api.util.checkAlreadyExists
import me.loghub.api.util.checkExists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SeriesStarService(
    private val userStarRepository: UserStarRepository,
    private val seriesRepository: SeriesRepository,
) : IStarService {
    @Transactional(readOnly = true)
    override fun existsStar(seriesId: Long, user: User): Boolean =
        userStarRepository.existsBySeriesIdAndUser(seriesId, user)

    @Transactional
    override fun addStar(seriesId: Long, user: User): UserStar {
        val series = seriesRepository.findById(seriesId)
            .orElseThrow { EntityNotFoundException(ResponseMessage.Series.NOT_FOUND) }

        checkAlreadyExists(userStarRepository.existsBySeriesAndUser(series, user)) {
            ResponseMessage.Star.ALREADY_EXISTS
        }

        series.incrementStarCount()
        return userStarRepository.save(UserStar(user = user, series = series, target = UserStar.Target.SERIES))
    }

    @Transactional
    override fun removeStar(seriesId: Long, user: User) {
        val affectedRows = userStarRepository.deleteBySeriesIdAndUser(seriesId, user)

        checkExists(affectedRows > 0) { ResponseMessage.Star.NOT_FOUND }

        seriesRepository.decrementStarCount(seriesId)
    }
}