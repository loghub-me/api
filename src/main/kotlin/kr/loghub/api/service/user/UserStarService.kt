package kr.loghub.api.service.user

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.star.StarDTO
import kr.loghub.api.mapper.star.StarMapper
import kr.loghub.api.repository.star.StarRepository
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.util.checkField
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserStarService(
    private val userRepository: UserRepository,
    private val starRepository: StarRepository,
) {
    companion object {
        private const val PAGE_SIZE = 20
    }

    fun getStars(username: String, page: Int): Page<StarDTO> {
        checkField("page", page > 0) { ResponseMessage.Page.MUST_BE_POSITIVE }

        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
        return starRepository.findByUser(user, PageRequest.of(page - 1, PAGE_SIZE))
            .map { StarMapper.map(it) }
    }
}