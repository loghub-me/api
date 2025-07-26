package kr.loghub.api.service.user

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.user.star.UserStarDTO
import kr.loghub.api.repository.user.UserStarRepository
import kr.loghub.api.util.checkField
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class UserStarService(private val userStarRepository: UserStarRepository) {
    private companion object {
        private const val PAGE_SIZE = 20
    }

    fun getStars(userId: Long, page: Int): Page<UserStarDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        return userStarRepository.findDTOsByUserId(userId, PageRequest.of(page - 1, PAGE_SIZE))
    }
}