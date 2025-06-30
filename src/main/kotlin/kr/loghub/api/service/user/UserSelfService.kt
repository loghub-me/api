package kr.loghub.api.service.user

import kr.loghub.api.constant.message.ResponseMessage
import kr.loghub.api.dto.task.avatar.AvatarRenameRequest
import kr.loghub.api.dto.user.UpdateUserPrivacyDTO
import kr.loghub.api.dto.user.UpdateUserProfileDTO
import kr.loghub.api.dto.user.UpdateUsernameDTO
import kr.loghub.api.entity.user.User
import kr.loghub.api.mapper.user.UserMapper
import kr.loghub.api.proxy.TaskAPIProxy
import kr.loghub.api.repository.article.ArticleRepository
import kr.loghub.api.repository.book.BookRepository
import kr.loghub.api.repository.question.QuestionRepository
import kr.loghub.api.repository.user.UserPostRepository
import kr.loghub.api.repository.user.UserRepository
import kr.loghub.api.util.checkAlreadyExists
import kr.loghub.api.util.requireNotEquals
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class UserSelfService(
    private val userRepository: UserRepository,
    private val userPostRepository: UserPostRepository,
    private val articleRepository: ArticleRepository,
    private val bookRepository: BookRepository,
    private val questionRepository: QuestionRepository,
    private val taskAPIProxy: TaskAPIProxy,
) {
    @Transactional(readOnly = true)
    fun getRecentPosts(user: User) = userPostRepository.findTop20ByUserOrderByUpdatedAtDesc(user)
        .map { UserMapper.mapPost(it) }

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
            UpdateUsernameDTO::newUsername.name,
            user.username, requestBody.newUsername,
        ) { ResponseMessage.User.USERNAME_NOT_CHANGED }
        checkAlreadyExists(
            UpdateUsernameDTO::newUsername.name,
            userRepository.existsByUsername(requestBody.newUsername)
        ) { ResponseMessage.User.USERNAME_ALREADY_EXISTS }

        val existingUser = userRepository.findByUsername(user.username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
        val (oldUsername, newUsername) = Pair(existingUser.username, requestBody.newUsername)

        existingUser.updateUsername(newUsername)
        taskAPIProxy.renameAvatar(AvatarRenameRequest(oldUsername, newUsername))
        articleRepository.updateWriterUsernameByWriterUsername(oldUsername, newUsername)
        bookRepository.updateWriterUsernameByWriterUsername(oldUsername, newUsername)
        questionRepository.updateWriterUsernameByWriterUsername(oldUsername, newUsername)
    }

    @Transactional
    fun updateAvatar(file: MultipartFile, user: User) =
        taskAPIProxy.uploadAvatar(file, user.username)

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