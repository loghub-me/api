package me.loghub.api.service.user

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.user.UpdateUserPrivacyDTO
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserPrivacy
import me.loghub.api.mapper.user.UserMapper
import me.loghub.api.repository.user.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserPrivacyService(private val userRepository: UserRepository) {
    @Transactional(readOnly = true)
    fun getPrivacy(user: User) = userRepository.findByUsername(user.username)
        ?.let { UserMapper.mapPrivacy(it.privacy) }
        ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)

    @Transactional
    fun updatePrivacy(requestBody: UpdateUserPrivacyDTO, user: User) {
        val foundUser = userRepository.findByUsername(user.username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
        val newPrivacy = UserPrivacy(emailPublic = requestBody.emailPublic)
        foundUser.updatePrivacy(newPrivacy)
    }
}