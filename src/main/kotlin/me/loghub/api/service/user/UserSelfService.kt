package me.loghub.api.service.user

import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.article.ArticleSort
import me.loghub.api.dto.user.UpdateUserPrivacyDTO
import me.loghub.api.dto.user.UpdateUserProfileDTO
import me.loghub.api.dto.user.UpdateUsernameDTO
import me.loghub.api.entity.user.User
import me.loghub.api.mapper.article.ArticleMapper
import me.loghub.api.mapper.user.UserMapper
import me.loghub.api.proxy.TaskAPIProxy
import me.loghub.api.repository.article.ArticleCustomRepository
import me.loghub.api.repository.article.ArticleRepository
import me.loghub.api.repository.question.QuestionRepository
import me.loghub.api.repository.series.SeriesRepository
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.util.checkConflict
import me.loghub.api.util.requireNotEquals
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class UserSelfService(
    private val userRepository: UserRepository,
    private val articleCustomRepository: ArticleCustomRepository,
    private val articleRepository: ArticleRepository,
    private val seriesRepository: SeriesRepository,
    private val questionRepository: QuestionRepository,
    private val taskAPIProxy: TaskAPIProxy,
) {
    private companion object {
        const val DEFAULT_ARTICLE_PAGE_SIZE = 20
    }

    @Transactional(readOnly = true)
    fun searchArticlesForImport(query: String, user: User) =
        articleCustomRepository.search(
            query = query,
            sort = ArticleSort.latest,
            pageable = PageRequest.of(0, DEFAULT_ARTICLE_PAGE_SIZE),
            username = user.username,
        ).toList().map { ArticleMapper.mapForImport(it) }

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
        checkConflict(
            UpdateUsernameDTO::newUsername.name,
            userRepository.existsByUsername(requestBody.newUsername)
        ) { ResponseMessage.User.USERNAME_ALREADY_EXISTS }

        val existingUser = userRepository.findByUsername(user.username)
            ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)
        val (oldUsername, newUsername) = Pair(existingUser.username, requestBody.newUsername)

        existingUser.updateUsername(newUsername)
        articleRepository.updateWriterUsernameByWriterUsername(oldUsername, newUsername)
        seriesRepository.updateWriterUsernameByWriterUsername(oldUsername, newUsername)
        questionRepository.updateWriterUsernameByWriterUsername(oldUsername, newUsername)
    }

    @Transactional
    fun updateAvatar(file: MultipartFile, user: User) =
        taskAPIProxy.uploadAvatar(file, user.id!!)

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