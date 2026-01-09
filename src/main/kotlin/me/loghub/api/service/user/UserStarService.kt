package me.loghub.api.service.user

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.user.star.UserStarDTO
import me.loghub.api.repository.user.UserStarRepository
import me.loghub.api.util.checkField
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserStarService(private val userStarRepository: UserStarRepository) {
    private companion object {
        private const val PAGE_SIZE = 20
    }

    @Transactional(readOnly = true)
    fun getStars(username: String, page: Int): Page<UserStarDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        val pageRequest = PageRequest.of(page - 1, PAGE_SIZE)
        return userStarRepository.findDTOsByStargazerUsername(username, pageRequest)
    }
}