package me.loghub.api.service.user

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.user.activity.UserActivityProjection
import me.loghub.api.dto.user.activity.UserActivitySummaryDTO
import me.loghub.api.repository.user.UserActivityRepository
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.util.checkField
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UserActivityService(
    private val userActivityRepository: UserActivityRepository,
    private val userRepository: UserRepository,
) {
    @Transactional(readOnly = true)
    fun getActivitySummaries(userId: Long, from: LocalDate, to: LocalDate): List<UserActivitySummaryDTO> {
        checkField("to", !to.isAfter(LocalDate.now())) { ResponseMessage.User.INVALID_DATE_RANGE }

        val user = userRepository.getReferenceById(userId)
        return userActivityRepository.findSummaryDTOsByUserAndCreatedDateBetween(user, from, to)
    }

    @Transactional(readOnly = true)
    fun getActivities(userId: Long, date: LocalDate): List<UserActivityProjection> {
        checkField("date", !date.isAfter(LocalDate.now())) { ResponseMessage.User.INVALID_DATE_RANGE }

        val user = userRepository.getReferenceById(userId)
        return userActivityRepository.findProjectionByUserIdAndCreatedDate(userId, date)
    }
}