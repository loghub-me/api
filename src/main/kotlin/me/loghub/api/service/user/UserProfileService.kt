package me.loghub.api.service.user

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.user.UpdateUserProfileDTO
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserProfile
import me.loghub.api.mapper.user.UserMapper
import me.loghub.api.repository.user.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserProfileService(private val userRepository: UserRepository) {
    @Transactional(readOnly = true)
    fun getProfile(user: User) = userRepository.findByUsername(user.username)
        ?.let { UserMapper.mapProfile(it) }
        ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)

    @Transactional
    fun updateProfile(requestBody: UpdateUserProfileDTO, user: User) {
        val foundUser = userRepository.findByUsername(user.username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
        val newProfile = UserProfile(nickname = requestBody.nickname, readme = requestBody.readme)
        foundUser.updateProfile(newProfile)
    }
}