package me.loghub.api.service.user

import feign.FeignException
import me.loghub.api.config.ClientConfig
import me.loghub.api.constant.message.ResponseMessage
import me.loghub.api.dto.user.UpdateUserGitHubDTO
import me.loghub.api.entity.user.User
import me.loghub.api.entity.user.UserGitHub
import me.loghub.api.exception.github.GitHubUserNotFoundException
import me.loghub.api.mapper.user.UserMapper
import me.loghub.api.proxy.GitHubAPIProxy
import me.loghub.api.repository.user.UserRepository
import me.loghub.api.util.checkConflict
import me.loghub.api.util.orElseThrowNotFound
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserGitHubService(
    private val userRepository: UserRepository,
    private val githubAPIProxy: GitHubAPIProxy,
) {
    private companion object {
        val LOGHUB_USER_PAGE_PREFIX = ClientConfig.HOST
    }

    @Transactional(readOnly = true)
    fun getGitHub(user: User) = userRepository.findByUsername(user.username)
        ?.let { UserMapper.mapGitHub(it.github) }
        ?: throw UsernameNotFoundException(ResponseMessage.User.NOT_FOUND)

    @Transactional
    fun updateGitHub(requestBody: UpdateUserGitHubDTO, user: User) {
        val foundUser = userRepository.findById(user.id!!)
            .orElseThrowNotFound { ResponseMessage.User.NOT_FOUND }
        val newGitHub = UserGitHub(requestBody.username)
        foundUser.updateGitHub(newGitHub)
    }

    @Transactional
    fun verifyGitHub(user: User) {
        val foundUser = userRepository.findById(user.id!!)
            .orElseThrowNotFound { ResponseMessage.User.NOT_FOUND }
        val github = foundUser.github
        val githubUsername = github.username

        checkConflict(githubUsername == null) {
            ResponseMessage.User.GitHub.USERNAME_NOT_SET
        }
        checkConflict(github.verified) {
            ResponseMessage.User.GitHub.ALREADY_VERIFIED
        }

        try {
            val userPageURL = "$LOGHUB_USER_PAGE_PREFIX/${foundUser.username}"
            val isVerified = checkGitHubProfile(userPageURL, githubUsername!!)
            if (!isVerified) {
                throw GitHubUserNotFoundException(ResponseMessage.User.GitHub.VERIFICATION_FAILED)
            }
            foundUser.updateGitHub(foundUser.github.copy(verified = true))
            foundUser.updateGitHub(foundUser.github.copy(verified = true))
        } catch (e: FeignException) {
            when (e.status()) {
                404 -> throw GitHubUserNotFoundException(ResponseMessage.User.GitHub.USER_NOT_FOUND)
                else -> throw e
            }
        }
    }

    private fun checkGitHubProfile(userPageURL: String, githubUsername: String): Boolean {
        val githubUser = githubAPIProxy.getUser(githubUsername)
        val socialAccounts = githubAPIProxy.getUserSocialAccounts(githubUsername)

        val matchedByBlog = githubUser.blog.contains(userPageURL, ignoreCase = true)
        val matchedBySocial = socialAccounts.any { it.url.contains(userPageURL, ignoreCase = true) }

        return matchedByBlog || matchedBySocial
    }
}