package kr.loghub.api.service.user

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.user.activity.UserActivityDTO
import kr.loghub.api.dto.user.activity.UserActivitySummaryDTO
import kr.loghub.api.repository.user.UserActivityRepository
import kr.loghub.api.util.checkField
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