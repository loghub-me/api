package me.loghub.api.service.user

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.user.activity.UserActivityDTO
import me.loghub.api.dto.user.activity.UserActivitySummaryDTO
import me.loghub.api.repository.user.UserActivityRepository
import me.loghub.api.util.checkField
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UserActivityService(private val userActivityRepository: UserActivityRepository) {
    @Transactional(readOnly = true)
    fun getActivitySummaries(userId: Long, from: LocalDate, to: LocalDate): List<UserActivitySummaryDTO> {
        checkField("to", !to.isAfter(LocalDate.now())) { ResponseMessage.User.INVALID_DATE_RANGE }

        return userActivityRepository.findSummaryDTOsByCreatedDateBetween(userId, from, to)
    }

    @Transactional(readOnly = true)
    fun getActivities(userId: Long, date: LocalDate): List<UserActivityDTO> {
        checkField("date", !date.isAfter(LocalDate.now())) { ResponseMessage.User.INVALID_DATE_RANGE }

        return userActivityRepository.findDTOByUserIdAndCreatedDate(userId, date)
    }
}