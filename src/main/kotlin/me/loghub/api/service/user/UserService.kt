package me.loghub.api.service.user

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.user.UserMapper
import me.loghub.api.repository.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(private val userRepository: UserRepository) {
    private companion object {
        private const val PAGE_SIZE = 20
    }

    @Transactional(readOnly = true)
    fun getUser(username: String) = userRepository.findByUsername(username)
        ?.let { UserMapper.mapDetail(it) }
        ?: throw EntityNotFoundException(ResponseMessage.User.NOT_FOUND)

    @Transactional(readOnly = true)
    fun getUserProfile(username: String) = userRepository.findByUsername(username)
        ?.let { UserMapper.mapProfile(it) }
        ?: throw EntityNotFoundException(ResponseMessage.User.NOT_FOUND)
}