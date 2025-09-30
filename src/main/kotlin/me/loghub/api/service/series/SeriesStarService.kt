package me.loghub.api.service.series

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserStar
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.user.UserStarRepository
import me.loghub.api.service.common.IStarService
import me.loghub.api.util.checkConflict
import me.loghub.api.util.checkExists
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SeriesStarService(
    private val userStarRepository: UserStarRepository,
    private val seriesRepository: SeriesRepository,
) : IStarService {
    @Transactional(readOnly = true)
    override fun existsStar(id: Long, user: User): Boolean {
        val seriesRef = seriesRepository.getReferenceById(id)
        return userStarRepository.existsBySeriesAndUser(seriesRef, user)
    }

    @Transactional
    override fun addStar(id: Long, user: User): UserStar {
        val seriesRef = seriesRepository.getReferenceById(id)

        checkConflict(
            userStarRepository.existsBySeriesAndUser(seriesRef, user)
        ) { ResponseMessage.Star.ALREADY_EXISTS }
        checkExists(
            seriesRepository.existsById(id)
        ) { ResponseMessage.Series.NOT_FOUND }

        seriesRepository.incrementStarCount(id)
        return userStarRepository.save(UserStar(user = user, series = seriesRef, target = UserStar.Target.SERIES))
    }

    @Transactional
    override fun deleteStar(id: Long, user: User) {
        val seriesRef = seriesRepository.getReferenceById(id)
        val deletedRows = userStarRepository.deleteBySeriesAndUser(seriesRef, user)

        checkExists(deletedRows > 0) { ResponseMessage.Star.NOT_FOUND }

        seriesRepository.decrementStarCount(id)
    }
}