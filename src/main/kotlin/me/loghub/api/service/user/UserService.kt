package me.loghub.api.service.user

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.user.UpdateUsernameDTO
import me.loghub.api.entity.user.User
import me.loghub.api.exception.entity.EntityNotFoundException
import me.loghub.api.mapper.user.UserMapper
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.util.checkConflict
import me.loghub.api.util.requireNotEquals
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class UserService(
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository,
    private val seriesRepository: SeriesRepository,
    private val questionRepository: QuestionRepository,
    private val taskAPIProxy: TaskAPIProxy
) {
    @Transactional(readOnly = true)
    fun getUser(username: String) = userRepository.findByUsername(username)
        ?.let { UserMapper.mapDetail(it) }
        ?: throw EntityNotFoundException(ResponseMessage.User.NOT_FOUND)

    @Transactional
    fun updateUsername(requestBody: UpdateUsernameDTO, user: User) {
        requireNotEquals(
            UpdateUsernameDTO::newUsername.name,
            user.username, requestBody.newUsername,
        ) { ResponseMessage.User.USERNAME_NOT_CHANGED }
        checkConflict(
            UpdateUsernameDTO::newUsername.name,
            userRepository.existsByUsername(requestBody.newUsername)
        ) { ResponseMessage.User.USERNAME_ALREADY_EXISTS }

        val foundUser = userRepository.findByUsername(user.username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
        val (oldUsername, newUsername) = Pair(foundUser.username, requestBody.newUsername)

        foundUser.updateUsername(newUsername)
        articleRepository.updateWriterUsernameByWriterUsername(oldUsername, newUsername)
        seriesRepository.updateWriterUsernameByWriterUsername(oldUsername, newUsername)
        questionRepository.updateWriterUsernameByWriterUsername(oldUsername, newUsername)
    }

    @Transactional
    fun updateAvatar(file: MultipartFile, user: User) =
        taskAPIProxy.uploadAvatar(file, user.id!!)
}