package kr.loghub.api.service.user

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.internal.avatar.AvatarRenameRequest
import kr.loghub.api.dto.user.UpdateUserPrivacyDTO
import kr.loghub.api.dto.user.UpdateUserProfileDTO
import kr.loghub.api.dto.user.UpdateUsernameDTO
import kr.loghub.api.entity.user.User
import kr.loghub.api.mapper.user.UserMapper
import kr.loghub.api.proxy.TaskAPIProxy
import kr.loghub.api.repository.article.ArticleRepository
import kr.loghub.api.repository.question.QuestionRepository
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.util.checkAlreadyExists
import kr.loghub.api.util.requireNotEquals
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserSelfService(
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository,
    private val questionRepository: QuestionRepository,
    private val taskAPIProxy: TaskAPIProxy,
) {
    @Transactional(readOnly = true)
    fun getProfile(user: User) = userRepository.findByUsername(user.username)
        ?.let { UserMapper.mapProfile(it) }
        ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)

    @Transactional(readOnly = true)
    fun getPrivacy(user: User) = userRepository.findByUsername(user.username)
        ?.let { UserMapper.mapPrivacy(it) }
        ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)

    @Transactional
    fun updateUsername(requestBody: UpdateUsernameDTO, user: User) {
        requireNotEquals(
            User::username.name,
            user.username, requestBody.username,
        ) { ResponseMessage.User.USERNAME_NOT_CHANGED }
        checkAlreadyExists(
            User::username.name,
            userRepository.existsByUsername(requestBody.username)
        ) { ResponseMessage.User.USERNAME_ALREADY_EXISTS }

        val existingUser = userRepository.findByUsername(user.username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
        val (oldUsername, newUsername) = Pair(existingUser.username, requestBody.username)

        existingUser.updateUsername(requestBody.username)
        taskAPIProxy.renameAvatar(AvatarRenameRequest(oldUsername, newUsername))
        articleRepository.updateWriterUsernameByWriterUsername(oldUsername, newUsername)
        questionRepository.updateWriterUsernameByWriterUsername(oldUsername, newUsername)
    }

    @Transactional
    fun updateProfile(requestBody: UpdateUserProfileDTO, user: User) {
        val existingUser = userRepository.findByUsername(user.username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
        existingUser.updateProfile(requestBody)
    }

    @Transactional
    fun updatePrivacy(requestBody: UpdateUserPrivacyDTO, user: User) {
        val existingUser = userRepository.findByUsername(user.username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
        existingUser.updatePrivacy(requestBody)
    }
}