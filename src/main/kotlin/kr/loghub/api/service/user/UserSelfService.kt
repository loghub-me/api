package kr.loghub.api.service.user

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.user.UpdateUserPrivacyDTO
import kr.loghub.api.dto.user.UpdateUserProfileDTO
import kr.loghub.api.entity.user.User
import kr.loghub.api.mapper.user.UserMapper
import kr.loghub.api.repository.user.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserSelfService(private val userRepository: UserRepository) {
    @Transactional(readOnly = true)
    fun getProfile(user: User) = userRepository.findByUsername(user.username)
        ?.let { UserMapper.mapProfile(it) }
        ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)

    @Transactional(readOnly = true)
    fun getPrivacy(user: User) = userRepository.findByUsername(user.username)
        ?.let { UserMapper.mapPrivacy(it) }
        ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)

    @Transactional
    fun updateProfile(requestBody: UpdateUserProfileDTO, user: User) {
        val user = userRepository.findByUsername(user.username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
        user.updateProfile(requestBody)
    }

    @Transactional
    fun updatePrivacy(requestBody: UpdateUserPrivacyDTO, user: User) {
        val user = userRepository.findByUsername(user.username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
        user.updatePrivacy(requestBody)
    }
}