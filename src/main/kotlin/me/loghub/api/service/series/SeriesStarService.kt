package me.loghub.api.service.series

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserStar
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.series.SeriesStatsRepository
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
    private val seriesStatsRepository: SeriesStatsRepository,
) : IStarService {
    @Transactional(readOnly = true)
    override fun existsStar(id: Long, stargazer: User): Boolean {
        val seriesRef = seriesRepository.getReferenceById(id)
        return userStarRepository.existsBySeriesAndStargazer(seriesRef, stargazer)
    }

    @Transactional
    override fun addStar(id: Long, stargazer: User): UserStar {
        val seriesRef = seriesRepository.getReferenceById(id)

        checkConflict(
            userStarRepository.existsBySeriesAndStargazer(seriesRef, stargazer)
        ) { ResponseMessage.Star.ALREADY_EXISTS }
        checkExists(
            seriesRepository.existsById(id)
        ) { ResponseMessage.Series.NOT_FOUND }

        seriesStatsRepository.incrementStarCount(id)
        val newStar = UserStar(stargazer = stargazer, series = seriesRef, target = UserStar.Target.SERIES);
        return userStarRepository.save(newStar)
    }

    @Transactional
    override fun deleteStar(id: Long, stargazer: User) {
        val seriesRef = seriesRepository.getReferenceById(id)
        val deletedRows = userStarRepository.deleteBySeriesAndStargazer(seriesRef, stargazer)

        checkExists(deletedRows > 0) { ResponseMessage.Star.NOT_FOUND }

        seriesStatsRepository.decrementStarCount(id)
    }
}