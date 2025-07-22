package kr.loghub.api.service.user

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.user.star.UserStarDTO
import kr.loghub.api.mapper.user.UserStarMapper
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.repository.user.UserStarRepository
import kr.loghub.api.util.checkField
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserStarService(
    private val userRepository: UserRepository,
    private val userStarRepository: UserStarRepository,
) {
    companion object {
        private const val PAGE_SIZE = 20
    }

    fun getStars(username: String, page: Int): Page<UserStarDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
        return userStarRepository.findAllByUser(user, PageRequest.of(page - 1, PAGE_SIZE))
            .map { UserStarMapper.map(it) }
    }
}