package kr.loghub.api.service.user

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.user.activity.UserActivityDTO
import kr.loghub.api.dto.user.activity.UserActivityDetailDTO
import kr.loghub.api.repository.user.UserActivityRepository
import kr.loghub.api.util.checkField
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UserActivityService(private val userActivityRepository: UserActivityRepository) {
    @Transactional(readOnly = true)
    fun getActivities(userId: Long, from: LocalDate, to: LocalDate): List<UserActivityDTO> {
        checkField("to", !to.isAfter(LocalDate.now())) { ResponseMessage.User.INVALID_DATE_RANGE }

        return userActivityRepository.findDTOs(userId, from, to)
    }

    @Transactional(readOnly = true)
    fun getActivity(userId: Long, date: LocalDate): List<UserActivityDetailDTO> {
        checkField("date", !date.isAfter(LocalDate.now())) { ResponseMessage.User.INVALID_DATE_RANGE }

        return userActivityRepository.findByUserIdAndCreatedDate(userId, date)
    }
}